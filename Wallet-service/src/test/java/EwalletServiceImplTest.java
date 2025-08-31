import com.eshoppingZone.ewallet.entity.Ewallet;
import com.eshoppingZone.ewallet.entity.Statement;
import com.eshoppingZone.ewallet.exception.InsufficientBalanceExcep;
import com.eshoppingZone.ewallet.exception.WalletException;
import com.eshoppingZone.ewallet.repository.EwalletRepository;
import com.eshoppingZone.ewallet.repository.StatementsRepository;
import com.eshoppingZone.ewallet.service.EwalletServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EwalletServiceImplTest {

    @Mock
    private EwalletRepository ewalletRepo;

    @Mock
    private StatementsRepository statementRepo;

    @InjectMocks
    private EwalletServiceImpl ewalletService;

    private Ewallet mockWallet;

    @BeforeEach
    void setUp() {
        mockWallet = new Ewallet();
        mockWallet.setUserId(1);
        mockWallet.setCurrentBalance(500.00);
    }

    @Test
    void testGetWallets_Success() {
        List<Ewallet> wallets = Arrays.asList(mockWallet);
        when(ewalletRepo.findAll()).thenReturn(wallets);

        List<Ewallet> response = ewalletService.getWallets();
        assertEquals(1, response.size());
    }

    @Test
    void testGetWallets_NoWallets() {
        when(ewalletRepo.findAll()).thenReturn(List.of());

        WalletException exception = assertThrows(WalletException.class, () -> ewalletService.getWallets());
        assertEquals("No wallets found", exception.getMessage());
    }

    @Test
    void testAddWallet_Success() {
        when(ewalletRepo.findByUserId(anyInt())).thenReturn(Optional.empty());
        when(ewalletRepo.save(any(Ewallet.class))).thenReturn(mockWallet);

        Ewallet response = ewalletService.addWallet(mockWallet);
        assertEquals(1, response.getUserId());
    }

    @Test
    void testAddMoney_Success() {
        when(ewalletRepo.findByUserId(anyInt())).thenReturn(Optional.of(mockWallet));
        when(ewalletRepo.save(any(Ewallet.class))).thenReturn(mockWallet);
        when(statementRepo.save(any(Statement.class))).thenReturn(new Statement());

        ewalletService.addMoney(1, 200, "Deposit");
        assertEquals(700.00, mockWallet.getCurrentBalance());
    }

    @Test
    void testUpdate_Success() {
        when(ewalletRepo.findByUserId(anyInt())).thenReturn(Optional.of(mockWallet));
        when(ewalletRepo.save(any(Ewallet.class))).thenReturn(mockWallet);
        when(statementRepo.save(any(Statement.class))).thenReturn(new Statement());

        ewalletService.update(1, 100, "Purchase", 101);
        assertEquals(400.00, mockWallet.getCurrentBalance());
    }

    @Test
    void testUpdate_InsufficientBalance() {
        when(ewalletRepo.findByUserId(anyInt())).thenReturn(Optional.of(mockWallet));

        InsufficientBalanceExcep exception = assertThrows(InsufficientBalanceExcep.class, () -> ewalletService.update(1, 600, "Purchase", 101));
        assertEquals("Insufficient Balance", exception.getMessage());
    }

    @Test
    void testGetByUserId_Success() {
        when(ewalletRepo.findByUserId(anyInt())).thenReturn(Optional.of(mockWallet));

        Ewallet response = ewalletService.getByUserId(1);
        assertEquals(1, response.getUserId());
    }

    @Test
    void testGetByUserId_NotFound() {
        when(ewalletRepo.findByUserId(anyInt())).thenReturn(Optional.empty());

        WalletException exception = assertThrows(WalletException.class, () -> ewalletService.getByUserId(1));
        assertEquals("Wallet not found. please add amount ", exception.getMessage());
    }

    @Test
    void testAddRefundAmount_Success() {
        when(ewalletRepo.findByUserId(anyInt())).thenReturn(Optional.of(mockWallet));
        when(ewalletRepo.save(any(Ewallet.class))).thenReturn(mockWallet);
        when(statementRepo.save(any(Statement.class))).thenReturn(new Statement());

        boolean result = ewalletService.addRefundAmount(1, 100, "Refund");
        assertTrue(result);
        assertEquals(600.00, mockWallet.getCurrentBalance());
    }
}
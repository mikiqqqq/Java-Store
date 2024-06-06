import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;

import static com.sun.jna.platform.win32.WinNT.KEY_WRITE;

public class WindowsRegistryReader {

    private static final int HKEY_CURRENT_USER = 0x80000001;
    private static final int KEY_READ = 0x20019;

    public interface Advapi32Lib extends Library {
        Advapi32Lib INSTANCE = Native.load("Advapi32", Advapi32Lib.class);

        int RegCreateKeyEx(HKEY hKey, String lpSubKey, int Reserved, String lpClass,
                           int dwOptions, int samDesired, int lpSecurityAttributes,
                           WinReg.HKEYByReference phkResult, IntByReference lpdwDisposition);

        int RegSetValueEx(HKEY hKey, String lpValueName, int Reserved, int dwType,
                          byte[] lpData, int cbData);

        int RegCloseKey(HKEY hKey);
    }

    public static void writeStringValue(String keyPath, String valueName, String value) {
        WinReg.HKEYByReference phkResult = new WinReg.HKEYByReference();
        IntByReference lpdwDisposition = new IntByReference();

        int rc = Advapi32Lib.INSTANCE.RegCreateKeyEx(Advapi32.HKEY_CURRENT_USER, keyPath, 0, null,
                0, KEY_WRITE, 0, phkResult, lpdwDisposition);

        if (rc != WinError.ERROR_SUCCESS) {
            System.err.println("RegCreateKeyEx failed: " + rc);
            return;
        }

        byte[] data = Native.toByteArray(value);
        rc = Advapi32Lib.INSTANCE.RegSetValueEx(phkResult.getValue(), valueName, 0, 1, data, data.length);

        if (rc != WinError.ERROR_SUCCESS) {
            System.err.println("RegSetValueEx failed: " + rc);
        }

        Advapi32Lib.INSTANCE.RegCloseKey(phkResult.getValue());
    }

    public static String readStringValue(String keyPath, String valueName) {
        WinReg.HKEYByReference phkResult = new WinReg.HKEYByReference();

        int rc = Advapi32Lib.INSTANCE.RegOpenKeyEx(Advapi32.HKEY_CURRENT_USER, keyPath, 0, KEY_READ, phkResult);
        if (rc != WinError.ERROR_SUCCESS) {
            System.err.println("RegOpenKeyEx failed: " + rc);
            return null;
        }

        byte[] data = new byte[1024];
        IntByReference dataLength = new IntByReference(data.length);
        rc = Advapi32Lib.INSTANCE.RegQueryValueEx(phkResult.getValue(), valueName, 0, null, data, dataLength);

        if (rc != WinError.ERROR_SUCCESS) {
            System.err.println("RegQueryValueEx failed: " + rc);
            return null;
        }

        Advapi32Lib.INSTANCE.RegCloseKey(phkResult.getValue());
        return Native.toString(data);
    }
}
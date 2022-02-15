package my.logon.screen.screens.ui.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import my.logon.screen.R;
import my.logon.screen.screens.data.LoginRepository;
import my.logon.screen.screens.data.Result;
import my.logon.screen.screens.data.model.LoggedInUser;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<my.logon.screen.screens.ui.login.LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<my.logon.screen.screens.ui.login.LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<my.logon.screen.screens.ui.login.LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<my.logon.screen.screens.ui.login.LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new my.logon.screen.screens.ui.login.LoginResult(new my.logon.screen.screens.ui.login.LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new my.logon.screen.screens.ui.login.LoginResult(new my.logon.screen.screens.ui.login.LoggedInUserView("123")));
        }
    }

    public void loginDataChanged(String username, String password) {

    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
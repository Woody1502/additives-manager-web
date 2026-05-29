using RecipeApp.WinForms.Services;

namespace RecipeApp.WinForms.Forms;

public class RegisterForm : Form
{
    private readonly AuthClient _auth;

    private readonly TextBox _loginBox    = new() { PlaceholderText = "Логин (мин. 3 символа)" };
    private readonly TextBox _passwordBox = new() { PlaceholderText = "Пароль (мин. 6 символов)", UseSystemPasswordChar = true };
    private readonly TextBox _confirmBox  = new() { PlaceholderText = "Повторите пароль",         UseSystemPasswordChar = true };
    private readonly Label   _statusLabel = new() { AutoSize = true, ForeColor = Color.Firebrick };

    public RegisterForm(AuthClient auth)
    {
        _auth = auth;
        Text = "Регистрация";
        Width = 380; Height = 260;
        StartPosition = FormStartPosition.CenterParent;
        FormBorderStyle = FormBorderStyle.FixedDialog;
        MaximizeBox = MinimizeBox = false;

        var title = new Label
        {
            Text = "Регистрация",
            AutoSize = true,
            Font = new Font(Font.FontFamily, 12, FontStyle.Bold),
            Top = 15, Left = 120
        };

        _loginBox.SetBounds(40, 50, 290, 28);
        _passwordBox.SetBounds(40, 84, 290, 28);
        _confirmBox.SetBounds(40, 118, 290, 28);
        _statusLabel.SetBounds(40, 152, 310, 20);

        var registerBtn = new Button { Text = "Зарегистрироваться" };
        registerBtn.SetBounds(40, 178, 290, 32);
        registerBtn.Click += OnRegister;

        Controls.AddRange([title, _loginBox, _passwordBox, _confirmBox, _statusLabel, registerBtn]);
    }

    private void OnRegister(object? s, EventArgs e)
    {
        _statusLabel.Text = string.Empty;

        if (_loginBox.Text.Trim().Length < 3)
        {
            _statusLabel.Text = "Логин должен быть не менее 3 символов."; return;
        }
        if (_passwordBox.Text.Length < 6)
        {
            _statusLabel.Text = "Пароль должен быть не менее 6 символов."; return;
        }
        if (_passwordBox.Text != _confirmBox.Text)
        {
            _statusLabel.Text = "Пароли не совпадают."; return;
        }

        var (response, error) = _auth.Register(_loginBox.Text.Trim(), _passwordBox.Text);
        if (error is not null)
        {
            _statusLabel.Text = error; return;
        }

        MessageBox.Show($"Аккаунт создан!\nЛогин: {response!.Login}", "Успех", MessageBoxButtons.OK, MessageBoxIcon.Information);
        DialogResult = DialogResult.OK;
    }
}

using RecipeApp.WinForms.Models;

namespace RecipeApp.WinForms.Forms;

public class ManufacturerEditorForm : Form
{
    private readonly TextBox _nameBox    = new();
    private readonly TextBox _countryBox = new();
    private readonly TextBox _addressBox = new() { Multiline = true, Height = 48, ScrollBars = ScrollBars.Vertical };
    private readonly TextBox _innBox     = new();
    private readonly TextBox _ogrnBox    = new();
    private readonly TextBox _phoneBox   = new();
    private readonly TextBox _emailBox   = new();
    private readonly TextBox _siteBox    = new();

    public ManufacturerEditorForm(Manufacturer? m = null)
    {
        Text = m is null ? "Добавить производителя" : "Редактировать производителя";
        Width = 500; Height = 420;
        StartPosition = FormStartPosition.CenterParent;
        FormBorderStyle = FormBorderStyle.FixedDialog;
        MaximizeBox = MinimizeBox = false;

        if (m is not null)
        {
            _nameBox.Text    = m.Name;
            _countryBox.Text = m.Country;
            _addressBox.Text = m.LegalAddress   ?? "";
            _innBox.Text     = m.Inn            ?? "";
            _ogrnBox.Text    = m.Ogrn           ?? "";
            _phoneBox.Text   = m.ContactPhone   ?? "";
            _emailBox.Text   = m.ContactEmail   ?? "";
            _siteBox.Text    = m.Website        ?? "";
        }

        BuildLayout();
    }

    private void BuildLayout()
    {
        int y = 10; int lw = 150; int fw = 290;

        void AddRow(string label, Control ctrl)
        {
            Controls.Add(new Label { Text = label, AutoSize = true, Left = 10, Top = y + 3 });
            ctrl.SetBounds(lw, y, fw, ctrl.Height < 23 ? 24 : ctrl.Height);
            Controls.Add(ctrl);
            y += ctrl.Height + 6;
        }

        AddRow("Название *",    _nameBox);
        AddRow("Страна *",      _countryBox);
        AddRow("Юр. адрес",     _addressBox);
        AddRow("ИНН",           _innBox);
        AddRow("ОГРН",          _ogrnBox);
        AddRow("Телефон",       _phoneBox);
        AddRow("Email",         _emailBox);
        AddRow("Сайт",          _siteBox);

        y += 10;
        var save   = new Button { Text = "Сохранить", Left = lw,       Top = y, Width = 100, Height = 30 };
        var cancel = new Button { Text = "Отмена",    Left = lw + 110, Top = y, Width = 100, Height = 30 };
        save.Click   += Save;
        cancel.Click += (_, _) => DialogResult = DialogResult.Cancel;
        Controls.Add(save); Controls.Add(cancel);
        ClientSize = new Size(460, y + 45);
    }

    private void Save(object? s, EventArgs e)
    {
        if (string.IsNullOrWhiteSpace(_nameBox.Text))
        {
            MessageBox.Show("Введите название.", "Проверка", MessageBoxButtons.OK, MessageBoxIcon.Warning); return;
        }
        if (string.IsNullOrWhiteSpace(_countryBox.Text))
        {
            MessageBox.Show("Введите страну.", "Проверка", MessageBoxButtons.OK, MessageBoxIcon.Warning); return;
        }
        DialogResult = DialogResult.OK;
    }

    public ManufacturerDto GetDto() => new()
    {
        Name         = _nameBox.Text.Trim(),
        Country      = _countryBox.Text.Trim(),
        LegalAddress = N(_addressBox.Text),
        Inn          = N(_innBox.Text),
        Ogrn         = N(_ogrnBox.Text),
        ContactPhone = N(_phoneBox.Text),
        ContactEmail = N(_emailBox.Text),
        Website      = N(_siteBox.Text)
    };

    private static string? N(string s) => string.IsNullOrWhiteSpace(s) ? null : s.Trim();
}

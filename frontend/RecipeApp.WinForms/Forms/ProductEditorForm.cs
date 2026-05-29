using RecipeApp.WinForms.Models;
using RecipeApp.WinForms.Services;

namespace RecipeApp.WinForms.Forms;

public class ProductEditorForm : Form
{
    private readonly TextBox          _nameBox        = new();
    private readonly ComboBox         _typeCombo      = new() { DropDownStyle = ComboBoxStyle.DropDownList, Width = 220 };
    private readonly ComboBox         _statusCombo    = new() { DropDownStyle = ComboBoxStyle.DropDownList, Width = 220 };
    private readonly TextBox          _eNumberBox     = new();
    private readonly TextBox          _tnVedBox       = new();
    private readonly TextBox          _releaseFormBox = new();
    private readonly TextBox          _sgrNumberBox   = new();
    private readonly TextBox          _sgrDateBox     = new() { PlaceholderText = "ГГГГ-ММ-ДД" };
    private readonly NumericUpDown    _shelfLife      = new() { Minimum = 0, Maximum = 999 };
    private readonly TextBox          _storageBox     = new() { Multiline = true, Height = 50, ScrollBars = ScrollBars.Vertical };
    private readonly CheckedListBox   _manuList       = new() { Height = 100 };

    private List<ProductType>   _types    = [];
    private List<ProductStatus> _statuses = [];
    private List<Manufacturer>  _manus    = [];

    public ProductEditorForm(ApiClient api, Product? product = null)
    {
        Text = product is null ? "Добавить продукт" : "Редактировать продукт";
        Width = 520; Height = 600;
        StartPosition = FormStartPosition.CenterParent;
        FormBorderStyle = FormBorderStyle.FixedDialog;
        MaximizeBox = MinimizeBox = false;
        AutoScroll = true;

        _types    = api.GetProductTypes();
        _statuses = api.GetProductStatuses();
        _manus    = api.GetManufacturers();

        _typeCombo.DataSource    = _types;
        _typeCombo.DisplayMember = "TypeName";
        _typeCombo.ValueMember   = "Id";

        _statusCombo.DataSource    = _statuses;
        _statusCombo.DisplayMember = "StatusName";
        _statusCombo.ValueMember   = "Id";

        foreach (var m in _manus) _manuList.Items.Add(m);

        if (product is not null)
        {
            _nameBox.Text        = product.ProductName;
            _eNumberBox.Text     = product.ENumber     ?? "";
            _tnVedBox.Text       = product.TnVedCode   ?? "";
            _releaseFormBox.Text = product.ReleaseForm  ?? "";
            _sgrNumberBox.Text   = product.SgrNumber    ?? "";
            _sgrDateBox.Text     = product.SgrRegistrationDate ?? "";
            _shelfLife.Value     = product.ShelfLifeMonths ?? 0;
            _storageBox.Text     = product.StorageConditions ?? "";

            if (product.ProductType is not null)
                _typeCombo.SelectedValue = product.ProductType.Id;
            if (product.Status is not null)
                _statusCombo.SelectedValue = product.Status.Id;

            for (int i = 0; i < _manus.Count; i++)
                if (product.Manufacturers.Any(m => m.Id == _manus[i].Id))
                    _manuList.SetItemChecked(i, true);
        }

        BuildLayout();
    }

    private void BuildLayout()
    {
        int y = 10, lw = 140, fw = 320;

        void AddRow(string label, Control ctrl)
        {
            Controls.Add(new Label { Text = label, AutoSize = true, Left = 10, Top = y + 3 });
            ctrl.SetBounds(lw, y, fw, ctrl.Height < 23 ? 24 : ctrl.Height);
            Controls.Add(ctrl);
            y += ctrl.Height + 6;
        }

        AddRow("Наименование *", _nameBox);
        AddRow("Тип *",           _typeCombo);
        AddRow("Статус *",        _statusCombo);
        AddRow("E-номер",         _eNumberBox);
        AddRow("ТН ВЭД",          _tnVedBox);
        AddRow("Форма выпуска",   _releaseFormBox);
        AddRow("№ СГР",           _sgrNumberBox);
        AddRow("Дата СГР",        _sgrDateBox);
        AddRow("Срок хр. (мес)", _shelfLife);
        AddRow("Условия хранения", _storageBox);
        AddRow("Производители",   _manuList);

        y += 10;
        var saveBtn   = new Button { Text = "Сохранить", Left = lw,       Top = y, Width = 100, Height = 30 };
        var cancelBtn = new Button { Text = "Отмена",    Left = lw + 110, Top = y, Width = 100, Height = 30 };

        saveBtn.Click   += Save;
        cancelBtn.Click += (_, _) => DialogResult = DialogResult.Cancel;

        Controls.Add(saveBtn);
        Controls.Add(cancelBtn);
        ClientSize = new Size(480, y + 45);
    }

    private void Save(object? s, EventArgs e)
    {
        if (string.IsNullOrWhiteSpace(_nameBox.Text))
        {
            MessageBox.Show("Введите наименование.", "Проверка", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            return;
        }
        if (_typeCombo.SelectedIndex < 0)
        {
            MessageBox.Show("Выберите тип продукта.", "Проверка", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            return;
        }
        if (_statusCombo.SelectedIndex < 0)
        {
            MessageBox.Show("Выберите статус.", "Проверка", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            return;
        }
        DialogResult = DialogResult.OK;
    }

    public ProductDto GetDto() => new()
    {
        ProductName        = _nameBox.Text.Trim(),
        ProductTypeId      = (int)_typeCombo.SelectedValue!,
        StatusId           = (int)_statusCombo.SelectedValue!,
        ENumber            = NullIfEmpty(_eNumberBox.Text),
        TnVedCode          = NullIfEmpty(_tnVedBox.Text),
        ReleaseForm        = NullIfEmpty(_releaseFormBox.Text),
        SgrNumber          = NullIfEmpty(_sgrNumberBox.Text),
        SgrRegistrationDate = NullIfEmpty(_sgrDateBox.Text),
        ShelfLifeMonths    = _shelfLife.Value > 0 ? (int)_shelfLife.Value : null,
        StorageConditions  = NullIfEmpty(_storageBox.Text),
        ManufacturerIds    = _manuList.CheckedItems.Cast<Manufacturer>().Select(m => m.Id).ToList()
    };

    private static string? NullIfEmpty(string s) => string.IsNullOrWhiteSpace(s) ? null : s.Trim();
}

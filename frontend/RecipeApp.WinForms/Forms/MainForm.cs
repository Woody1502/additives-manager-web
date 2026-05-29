using RecipeApp.WinForms.Models;
using RecipeApp.WinForms.Services;

namespace RecipeApp.WinForms.Forms;

public class MainForm : Form
{
    private readonly ApiClient  _api;
    private readonly AuthClient _auth;
    private bool _isAdmin;

    // ── Login ──────────────────────────────────────────────────────────────
    private readonly Panel _loginPanel = new() { Dock = DockStyle.Fill };
    private readonly TextBox _loginBox    = new() { PlaceholderText = "Логин" };
    private readonly TextBox _passwordBox = new() { PlaceholderText = "Пароль", UseSystemPasswordChar = true };
    private readonly Label   _loginStatus = new() { AutoSize = true };

    // ── Main ───────────────────────────────────────────────────────────────
    private readonly Panel      _mainPanel = new() { Dock = DockStyle.Fill, Visible = false };
    private readonly TabControl _tabs      = new() { Dock = DockStyle.Fill };
    private readonly Label      _statusBar = new() { Dock = DockStyle.Bottom, Height = 24, ForeColor = Color.DarkSlateBlue };

    // Products
    private readonly DataGridView _productsGrid  = MakeGrid();
    private readonly TextBox      _productSearch = new() { PlaceholderText = "Поиск по наименованию…", Width = 260 };
    private List<Product> _allProducts = [];

    // Manufacturers
    private readonly DataGridView _manuGrid = MakeGrid();

    // Product Types
    private readonly DataGridView _typesGrid = MakeGrid();

    // Statuses
    private readonly DataGridView _statusesGrid = MakeGrid();

    public MainForm(ApiClient api, AuthClient auth)
    {
        _api  = api;
        _auth = auth;
        Text = "Менеджер пищевых добавок";
        Width  = 1200;
        Height = 720;
        MinimumSize = new Size(900, 600);
        StartPosition = FormStartPosition.CenterScreen;

        BuildLoginPanel();
        BuildMainPanel();
        Controls.Add(_loginPanel);
        Controls.Add(_mainPanel);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  LOGIN
    // ═══════════════════════════════════════════════════════════════════════
    private void BuildLoginPanel()
    {
        var title = new Label
        {
            Text = "Менеджер пищевых добавок",
            AutoSize = true,
            Font = new Font(Font.FontFamily, 16, FontStyle.Bold)
        };

        var loginBtn    = new Button { Text = "Войти",            Width = 260 };
        var registerBtn = new Button { Text = "Зарегистрироваться", Width = 260 };
        loginBtn.Click    += OnLogin;
        registerBtn.Click += (_, _) =>
        {
            using var form = new RegisterForm(_auth);
            form.ShowDialog(this);
        };

        _loginBox.Width    = 260;
        _passwordBox.Width = 260;

        // vertical stack via TableLayout
        var tbl = new TableLayoutPanel
        {
            RowCount = 5, ColumnCount = 1,
            AutoSize = true,
            Anchor = AnchorStyles.None
        };
        tbl.RowCount = 6;
        tbl.RowStyles.Add(new RowStyle(SizeType.AutoSize));
        tbl.RowStyles.Add(new RowStyle(SizeType.Absolute, 36));
        tbl.RowStyles.Add(new RowStyle(SizeType.Absolute, 36));
        tbl.RowStyles.Add(new RowStyle(SizeType.Absolute, 40));
        tbl.RowStyles.Add(new RowStyle(SizeType.Absolute, 40));
        tbl.RowStyles.Add(new RowStyle(SizeType.AutoSize));
        tbl.Controls.Add(title, 0, 0);
        tbl.Controls.Add(_loginBox, 0, 1);
        tbl.Controls.Add(_passwordBox, 0, 2);
        tbl.Controls.Add(loginBtn, 0, 3);
        tbl.Controls.Add(registerBtn, 0, 4);
        tbl.Controls.Add(_loginStatus, 0, 5);

        _loginPanel.Controls.Add(tbl);
        _loginPanel.Resize += (_, _) =>
        {
            tbl.Left = (_loginPanel.Width  - tbl.Width)  / 2;
            tbl.Top  = (_loginPanel.Height - tbl.Height) / 2;
        };
    }

    private void OnLogin(object? s, EventArgs e)
    {
        var (response, error) = _auth.Login(_loginBox.Text.Trim(), _passwordBox.Text);
        if (error is not null)
        {
            _loginStatus.Text      = error;
            _loginStatus.ForeColor = Color.Firebrick;
            return;
        }

        _api.SetToken(response!.Token);
        _isAdmin = response.Role == "ADMIN";
        Text = $"Менеджер пищевых добавок — {response.Login} ({response.Role})";
        _loginPanel.Visible = false;
        _mainPanel.Visible  = true;

        // show/hide admin-only tabs
        if (!_isAdmin)
        {
            _tabs.TabPages.Remove(_tabs.TabPages["types"]!);
            _tabs.TabPages.Remove(_tabs.TabPages["statuses"]!);
        }

        LoadProducts();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  MAIN PANEL
    // ═══════════════════════════════════════════════════════════════════════
    private void BuildMainPanel()
    {
        var logoutBtn = new Button { Text = "Выйти", Dock = DockStyle.Right, Width = 90 };
        logoutBtn.Click += (_, _) =>
        {
            _passwordBox.Clear();
            _loginStatus.Text = string.Empty;
            Text = "Менеджер пищевых добавок";
            _mainPanel.Visible  = false;
            _loginPanel.Visible = true;
        };

        var topBar = new Panel { Dock = DockStyle.Top, Height = 36, Padding = new Padding(4) };
        topBar.Controls.Add(logoutBtn);
        _mainPanel.Controls.Add(_statusBar);
        _mainPanel.Controls.Add(_tabs);
        _mainPanel.Controls.Add(topBar);

        BuildProductsTab();
        BuildManufacturersTab();
        BuildTypesTab();
        BuildStatusesTab();
    }

    // ─── Products ──────────────────────────────────────────────────────────
    private void BuildProductsTab()
    {
        var tab = new TabPage("Продукты") { Name = "products" };

        var refreshBtn = new Button { Text = "Обновить", Width = 90 };
        var addBtn     = new Button { Text = "Добавить", Width = 90 };
        var searchBtn  = new Button { Text = "Найти",    Width = 80 };
        refreshBtn.Click += (_, _) => LoadProducts();
        addBtn.Click     += (_, _) => AddProduct();
        searchBtn.Click  += (_, _) => FilterProducts();
        _productSearch.KeyDown += (_, e) => { if (e.KeyCode == Keys.Enter) FilterProducts(); };

        var toolbar = new FlowLayoutPanel { Dock = DockStyle.Top, Height = 38, FlowDirection = FlowDirection.LeftToRight, Padding = new Padding(4, 4, 0, 0) };
        toolbar.Controls.AddRange([_productSearch, searchBtn, addBtn, refreshBtn]);

        ConfigureProductsGrid();
        _productsGrid.Dock = DockStyle.Fill;
        _productsGrid.CellContentClick += (_, e) => OnProductGridClick(e);

        tab.Controls.Add(_productsGrid);
        tab.Controls.Add(toolbar);
        _tabs.TabPages.Add(tab);
    }

    private void ConfigureProductsGrid()
    {
        _productsGrid.Columns.AddRange(
            TextCol("Id",          "ID",           60),
            TextCol("ProductName", "Наименование", 200),
            TextCol("ENumber",     "E-номер",       80),
            TextCol("TypeName",    "Тип",          120),
            TextCol("StatusName",  "Статус",       120),
            TextCol("SgrNumber",   "№ СГР",        110),
            TextCol("ShelfLife",   "Срок (мес)",    80),
            BtnCol ("Edit",   "Редакт.",  100),
            BtnCol ("Delete", "Удалить",   80)
        );
    }

    private void LoadProducts()
    {
        _allProducts = _api.GetProducts();
        RenderProducts(_allProducts);
        SetStatus($"Продуктов: {_allProducts.Count}");
    }

    private void FilterProducts()
    {
        var q = _productSearch.Text.Trim();
        var filtered = string.IsNullOrEmpty(q)
            ? _allProducts
            : _allProducts.Where(p => p.ProductName.Contains(q, StringComparison.OrdinalIgnoreCase)).ToList();
        RenderProducts(filtered);
    }

    private void RenderProducts(List<Product> products)
    {
        var rows = products.Select(p => new
        {
            p.Id,
            p.ProductName,
            p.ENumber,
            TypeName   = p.ProductType?.TypeName   ?? "",
            StatusName = p.Status?.StatusName       ?? "",
            p.SgrNumber,
            ShelfLife  = p.ShelfLifeMonths?.ToString() ?? ""
        }).ToList();
        _productsGrid.DataSource = rows;
    }

    private void OnProductGridClick(DataGridViewCellEventArgs e)
    {
        if (e.RowIndex < 0) return;
        if (!int.TryParse(_productsGrid.Rows[e.RowIndex].Cells["Id"].Value?.ToString(), out var id)) return;
        var col = _productsGrid.Columns[e.ColumnIndex].Name;
        if (col == "Edit" && _isAdmin)   EditProduct(id);
        if (col == "Delete" && _isAdmin) DeleteProduct(id);
    }

    private void AddProduct()
    {
        using var form = new ProductEditorForm(_api);
        if (form.ShowDialog(this) != DialogResult.OK) return;
        _api.CreateProduct(form.GetDto());
        LoadProducts();
        SetStatus("Продукт добавлен.");
    }

    private void EditProduct(int id)
    {
        var product = _allProducts.FirstOrDefault(p => p.Id == id);
        if (product is null) return;
        using var form = new ProductEditorForm(_api, product);
        if (form.ShowDialog(this) != DialogResult.OK) return;
        _api.UpdateProduct(id, form.GetDto());
        LoadProducts();
        SetStatus($"Продукт ID={id} обновлён.");
    }

    private void DeleteProduct(int id)
    {
        if (MessageBox.Show($"Удалить продукт ID={id}?", "Подтверждение", MessageBoxButtons.YesNo, MessageBoxIcon.Question) != DialogResult.Yes) return;
        _api.DeleteProduct(id);
        LoadProducts();
        SetStatus($"Продукт ID={id} удалён.");
    }

    // ─── Manufacturers ─────────────────────────────────────────────────────
    private void BuildManufacturersTab()
    {
        var tab = new TabPage("Производители") { Name = "manu" };
        var addBtn     = new Button { Text = "Добавить", Width = 90 };
        var refreshBtn = new Button { Text = "Обновить", Width = 90 };
        addBtn.Click     += (_, _) => AddManufacturer();
        refreshBtn.Click += (_, _) => LoadManufacturers();

        var toolbar = new FlowLayoutPanel { Dock = DockStyle.Top, Height = 38, FlowDirection = FlowDirection.LeftToRight, Padding = new Padding(4, 4, 0, 0) };
        toolbar.Controls.AddRange([addBtn, refreshBtn]);

        _manuGrid.Columns.AddRange(
            TextCol("Id",      "ID",      50),
            TextCol("Name",    "Название",160),
            TextCol("Country", "Страна",  90),
            TextCol("Inn",     "ИНН",     110),
            TextCol("Ogrn",    "ОГРН",    130),
            TextCol("Phone",   "Телефон", 120),
            TextCol("Email",   "Email",   150),
            BtnCol ("Edit",   "Редакт.", 100),
            BtnCol ("Delete", "Удалить",  80)
        );
        _manuGrid.Dock = DockStyle.Fill;
        _manuGrid.CellContentClick += (_, e) => OnManuGridClick(e);

        tab.Controls.Add(_manuGrid);
        tab.Controls.Add(toolbar);
        _tabs.TabPages.Add(tab);
    }

    private void LoadManufacturers()
    {
        var list = _api.GetManufacturers();
        _manuGrid.DataSource = list.Select(m => new
        {
            m.Id, m.Name, m.Country, m.Inn, m.Ogrn,
            Phone = m.ContactPhone ?? "", Email = m.ContactEmail ?? ""
        }).ToList();
        SetStatus($"Производителей: {list.Count}");
    }

    private void OnManuGridClick(DataGridViewCellEventArgs e)
    {
        if (e.RowIndex < 0) return;
        if (!int.TryParse(_manuGrid.Rows[e.RowIndex].Cells["Id"].Value?.ToString(), out var id)) return;
        var col = _manuGrid.Columns[e.ColumnIndex].Name;
        if (col == "Edit")
        {
            var list = _api.GetManufacturers();
            var m = list.FirstOrDefault(x => x.Id == id);
            if (m is null) return;
            using var form = new ManufacturerEditorForm(m);
            if (form.ShowDialog(this) != DialogResult.OK) return;
            _api.UpdateManufacturer(id, form.GetDto());
            LoadManufacturers();
            SetStatus($"Производитель ID={id} обновлён.");
        }
        else if (col == "Delete")
        {
            if (MessageBox.Show($"Удалить производителя ID={id}?", "Подтверждение", MessageBoxButtons.YesNo, MessageBoxIcon.Question) != DialogResult.Yes) return;
            _api.DeleteManufacturer(id);
            LoadManufacturers();
            SetStatus($"Производитель ID={id} удалён.");
        }
    }

    private void AddManufacturer()
    {
        using var form = new ManufacturerEditorForm();
        if (form.ShowDialog(this) != DialogResult.OK) return;
        _api.CreateManufacturer(form.GetDto());
        LoadManufacturers();
        SetStatus("Производитель добавлен.");
    }

    // ─── Product Types ─────────────────────────────────────────────────────
    private void BuildTypesTab()
    {
        var tab = new TabPage("Типы") { Name = "types" };
        BuildSimpleRefTab(tab, _typesGrid,
            new[] { TextCol("Id", "ID", 50), TextCol("TypeName", "Название", 200), TextCol("Description", "Описание", 340) },
            () => {
                var list = _api.GetProductTypes();
                _typesGrid.DataSource = list.Select(t => new { t.Id, t.TypeName, Description = t.Description ?? "" }).ToList();
                SetStatus($"Типов: {list.Count}");
            },
            () => {
                using var f = new SimpleRefEditorForm("Тип продукта", "Название типа");
                if (f.ShowDialog(this) != DialogResult.OK) return;
                _api.CreateProductType(new ProductTypeDto { TypeName = f.NameValue, Description = f.DescriptionValue });
                _tabs.SelectedTab!.Tag = "refresh";
            },
            (id) => {
                var list = _api.GetProductTypes();
                var t = list.FirstOrDefault(x => x.Id == id);
                if (t is null) return;
                using var f = new SimpleRefEditorForm("Тип продукта", "Название типа", t.TypeName, t.Description);
                if (f.ShowDialog(this) != DialogResult.OK) return;
                _api.UpdateProductType(id, new ProductTypeDto { TypeName = f.NameValue, Description = f.DescriptionValue });
                SetStatus($"Тип ID={id} обновлён.");
            },
            (id) => {
                if (MessageBox.Show($"Удалить тип ID={id}?", "Подтверждение", MessageBoxButtons.YesNo, MessageBoxIcon.Question) != DialogResult.Yes) return;
                _api.DeleteProductType(id);
                SetStatus($"Тип ID={id} удалён.");
            }
        );
    }

    // ─── Statuses ──────────────────────────────────────────────────────────
    private void BuildStatusesTab()
    {
        var tab = new TabPage("Статусы") { Name = "statuses" };
        BuildSimpleRefTab(tab, _statusesGrid,
            new[] { TextCol("Id", "ID", 50), TextCol("StatusName", "Название", 200), TextCol("Description", "Описание", 340) },
            () => {
                var list = _api.GetProductStatuses();
                _statusesGrid.DataSource = list.Select(s => new { s.Id, s.StatusName, Description = s.Description ?? "" }).ToList();
                SetStatus($"Статусов: {list.Count}");
            },
            () => {
                using var f = new SimpleRefEditorForm("Статус продукта", "Название статуса");
                if (f.ShowDialog(this) != DialogResult.OK) return;
                _api.CreateProductStatus(new ProductStatusDto { StatusName = f.NameValue, Description = f.DescriptionValue });
                _tabs.SelectedTab!.Tag = "refresh";
            },
            (id) => {
                var list = _api.GetProductStatuses();
                var s = list.FirstOrDefault(x => x.Id == id);
                if (s is null) return;
                using var f = new SimpleRefEditorForm("Статус продукта", "Название статуса", s.StatusName, s.Description);
                if (f.ShowDialog(this) != DialogResult.OK) return;
                _api.UpdateProductStatus(id, new ProductStatusDto { StatusName = f.NameValue, Description = f.DescriptionValue });
                SetStatus($"Статус ID={id} обновлён.");
            },
            (id) => {
                if (MessageBox.Show($"Удалить статус ID={id}?", "Подтверждение", MessageBoxButtons.YesNo, MessageBoxIcon.Question) != DialogResult.Yes) return;
                _api.DeleteProductStatus(id);
                SetStatus($"Статус ID={id} удалён.");
            }
        );
    }

    private void BuildSimpleRefTab(TabPage tab, DataGridView grid,
        DataGridViewColumn[] cols, Action load, Action add, Action<int> edit, Action<int> delete)
    {
        grid.Columns.AddRange(cols);
        grid.Columns.AddRange(BtnCol("Edit", "Редакт.", 100), BtnCol("Delete", "Удалить", 80));
        grid.Dock = DockStyle.Fill;

        load();

        grid.CellContentClick += (_, e) =>
        {
            if (e.RowIndex < 0) return;
            if (!int.TryParse(grid.Rows[e.RowIndex].Cells["Id"].Value?.ToString(), out var id)) return;
            var col = grid.Columns[e.ColumnIndex].Name;
            if (col == "Edit")   { edit(id); load(); }
            if (col == "Delete") { delete(id); load(); }
        };

        var addBtn     = new Button { Text = "Добавить", Width = 90 };
        var refreshBtn = new Button { Text = "Обновить", Width = 90 };
        addBtn.Click     += (_, _) => { add(); load(); };
        refreshBtn.Click += (_, _) => load();

        var toolbar = new FlowLayoutPanel { Dock = DockStyle.Top, Height = 38, FlowDirection = FlowDirection.LeftToRight, Padding = new Padding(4, 4, 0, 0) };
        toolbar.Controls.AddRange([addBtn, refreshBtn]);

        tab.Controls.Add(grid);
        tab.Controls.Add(toolbar);
        _tabs.TabPages.Add(tab);
    }

    // ─── Helpers ───────────────────────────────────────────────────────────
    private void SetStatus(string msg) => _statusBar.Text = "  " + msg;

    private static DataGridView MakeGrid() => new()
    {
        ReadOnly = true, AllowUserToAddRows = false, AutoGenerateColumns = false,
        SelectionMode = DataGridViewSelectionMode.FullRowSelect,
        AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.None
    };

    private static DataGridViewTextBoxColumn TextCol(string name, string header, int width) => new()
    { Name = name, HeaderText = header, DataPropertyName = name, Width = width };

    private static DataGridViewButtonColumn BtnCol(string name, string text, int width) => new()
    { Name = name, HeaderText = text, Text = text, UseColumnTextForButtonValue = true, Width = width };
}

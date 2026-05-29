namespace RecipeApp.WinForms.Forms;

/// <summary>Generic editor popup for ProductType and ProductStatus.</summary>
public class SimpleRefEditorForm : Form
{
    private readonly TextBox _nameBox = new();
    private readonly TextBox _descBox = new() { Multiline = true, Height = 60, ScrollBars = ScrollBars.Vertical };

    public string  NameValue        => _nameBox.Text.Trim();
    public string? DescriptionValue => string.IsNullOrWhiteSpace(_descBox.Text) ? null : _descBox.Text.Trim();

    public SimpleRefEditorForm(string title, string nameLabel, string? existingName = null, string? existingDesc = null)
    {
        Text = existingName is null ? $"Добавить: {title}" : $"Редактировать: {title}";
        Width = 420; Height = 230;
        StartPosition = FormStartPosition.CenterParent;
        FormBorderStyle = FormBorderStyle.FixedDialog;
        MaximizeBox = MinimizeBox = false;

        _nameBox.Text = existingName ?? "";
        _descBox.Text = existingDesc ?? "";

        int y = 14; int lw = 130; int fw = 250;

        void AddRow(string label, Control ctrl)
        {
            Controls.Add(new Label { Text = label, AutoSize = true, Left = 10, Top = y + 3 });
            ctrl.SetBounds(lw, y, fw, ctrl.Height < 23 ? 24 : ctrl.Height);
            Controls.Add(ctrl);
            y += ctrl.Height + 8;
        }

        AddRow(nameLabel + " *", _nameBox);
        AddRow("Описание",       _descBox);

        y += 6;
        var save   = new Button { Text = "Сохранить", Left = lw,       Top = y, Width = 100, Height = 30 };
        var cancel = new Button { Text = "Отмена",    Left = lw + 110, Top = y, Width = 100, Height = 30 };

        save.Click   += (_, _) =>
        {
            if (string.IsNullOrWhiteSpace(_nameBox.Text))
            {
                MessageBox.Show($"Введите {nameLabel.ToLower()}.", "Проверка", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }
            DialogResult = DialogResult.OK;
        };
        cancel.Click += (_, _) => DialogResult = DialogResult.Cancel;

        Controls.Add(save); Controls.Add(cancel);
        ClientSize = new Size(390, y + 45);
    }
}

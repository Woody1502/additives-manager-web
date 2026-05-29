namespace RecipeApp.WinForms.Models;

public class ProductStatus
{
    public int Id { get; set; }
    public string StatusName { get; set; } = string.Empty;
    public string? Description { get; set; }
    public override string ToString() => StatusName;
}

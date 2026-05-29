namespace RecipeApp.WinForms.Models;

public class ProductType
{
    public int Id { get; set; }
    public string TypeName { get; set; } = string.Empty;
    public string? Description { get; set; }
    public override string ToString() => TypeName;
}

namespace RecipeApp.WinForms.Models;

public class Product
{
    public int Id { get; set; }
    public string ProductName { get; set; } = string.Empty;
    public ProductType? ProductType { get; set; }
    public string? TnVedCode { get; set; }
    public string? ENumber { get; set; }
    public string? ReleaseForm { get; set; }
    public List<Manufacturer> Manufacturers { get; set; } = [];
    public string? SgrNumber { get; set; }
    public string? SgrRegistrationDate { get; set; }
    public int? ShelfLifeMonths { get; set; }
    public string? StorageConditions { get; set; }
    public ProductStatus? Status { get; set; }
    public DateTime? CreatedAt { get; set; }
    public DateTime? UpdatedAt { get; set; }
}

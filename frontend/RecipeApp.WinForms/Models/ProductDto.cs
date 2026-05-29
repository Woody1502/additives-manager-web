namespace RecipeApp.WinForms.Models;

public class ProductDto
{
    public string ProductName { get; set; } = string.Empty;
    public int ProductTypeId { get; set; }
    public int StatusId { get; set; }
    public string? TnVedCode { get; set; }
    public string? ENumber { get; set; }
    public string? ReleaseForm { get; set; }
    public List<int>? ManufacturerIds { get; set; }
    public string? SgrNumber { get; set; }
    public string? SgrRegistrationDate { get; set; }
    public int? ShelfLifeMonths { get; set; }
    public string? StorageConditions { get; set; }
}

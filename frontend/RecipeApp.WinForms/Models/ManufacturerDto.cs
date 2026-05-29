namespace RecipeApp.WinForms.Models;

public class ManufacturerDto
{
    public string Name { get; set; } = string.Empty;
    public string Country { get; set; } = string.Empty;
    public string? LegalAddress { get; set; }
    public string? Inn { get; set; }
    public string? Ogrn { get; set; }
    public string? ContactPhone { get; set; }
    public string? ContactEmail { get; set; }
    public string? Website { get; set; }
}

using System.Diagnostics;
using System.Net.Http.Json;
using System.Text.Json;
using RecipeApp.WinForms.Models;

namespace RecipeApp.WinForms.Services;

public sealed class ApiClient
{
    private readonly HttpClient _http;
    private static readonly JsonSerializerOptions Options = new()
    {
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
        PropertyNameCaseInsensitive = true
    };

    public ApiClient(string baseUrl)
    {
        _http = new HttpClient { BaseAddress = new Uri(baseUrl.TrimEnd('/') + "/") };
    }

    public void SetToken(string token)
    {
        _http.DefaultRequestHeaders.Authorization =
            new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", token);
    }

    public List<Product> GetProducts() => Get<List<Product>>("api/products/list") ?? [];
    public Product? CreateProduct(ProductDto dto) => Post<ProductDto, Product>("api/products", dto);
    public Product? UpdateProduct(int id, ProductDto dto) => Put<ProductDto, Product>($"api/products/update/{id}", dto);
    public bool DeleteProduct(int id) => Delete($"api/products/{id}");

    public List<Manufacturer> GetManufacturers() => Get<List<Manufacturer>>("api/manufacturers") ?? [];
    public Manufacturer? CreateManufacturer(ManufacturerDto dto) => Post<ManufacturerDto, Manufacturer>("api/manufacturers", dto);
    public Manufacturer? UpdateManufacturer(int id, ManufacturerDto dto) => Put<ManufacturerDto, Manufacturer>($"api/manufacturers/{id}", dto);
    public bool DeleteManufacturer(int id) => Delete($"api/manufacturers/{id}");

    public List<ProductType> GetProductTypes() => Get<List<ProductType>>("api/product-types") ?? [];
    public ProductType? CreateProductType(ProductTypeDto dto) => Post<ProductTypeDto, ProductType>("api/product-types", dto);
    public ProductType? UpdateProductType(int id, ProductTypeDto dto) => Put<ProductTypeDto, ProductType>($"api/product-types/{id}", dto);
    public bool DeleteProductType(int id) => Delete($"api/product-types/{id}");

    public List<ProductStatus> GetProductStatuses() => Get<List<ProductStatus>>("api/product-statuses") ?? [];
    public ProductStatus? CreateProductStatus(ProductStatusDto dto) => Post<ProductStatusDto, ProductStatus>("api/product-statuses", dto);
    public ProductStatus? UpdateProductStatus(int id, ProductStatusDto dto) => Put<ProductStatusDto, ProductStatus>($"api/product-statuses/{id}", dto);
    public bool DeleteProductStatus(int id) => Delete($"api/product-statuses/{id}");

    private T? Get<T>(string path)
    {
        try
        {
            var r = _http.GetAsync(path).GetAwaiter().GetResult();
            if (!r.IsSuccessStatusCode) return default;
            return r.Content.ReadFromJsonAsync<T>(Options).GetAwaiter().GetResult();
        }
        catch (Exception ex) { Debug.WriteLine($"[GET {path}] {ex.Message}"); return default; }
    }

    private TRes? Post<TReq, TRes>(string path, TReq body)
    {
        try
        {
            var r = _http.PostAsJsonAsync(path, body, Options).GetAwaiter().GetResult();
            if (!r.IsSuccessStatusCode) return default;
            return r.Content.ReadFromJsonAsync<TRes>(Options).GetAwaiter().GetResult();
        }
        catch (Exception ex) { Debug.WriteLine($"[POST {path}] {ex.Message}"); return default; }
    }

    private TRes? Put<TReq, TRes>(string path, TReq body)
    {
        try
        {
            var r = _http.PutAsJsonAsync(path, body, Options).GetAwaiter().GetResult();
            if (!r.IsSuccessStatusCode) return default;
            return r.Content.ReadFromJsonAsync<TRes>(Options).GetAwaiter().GetResult();
        }
        catch (Exception ex) { Debug.WriteLine($"[PUT {path}] {ex.Message}"); return default; }
    }

    private bool Delete(string path)
    {
        try
        {
            return _http.DeleteAsync(path).GetAwaiter().GetResult().IsSuccessStatusCode;
        }
        catch (Exception ex) { Debug.WriteLine($"[DELETE {path}] {ex.Message}"); return false; }
    }
}

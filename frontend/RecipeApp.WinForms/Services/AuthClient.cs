using System.Diagnostics;
using System.Net.Http.Json;
using System.Text.Json;
using RecipeApp.WinForms.Models;

namespace RecipeApp.WinForms.Services;

public sealed class AuthClient
{
    private readonly HttpClient _http;
    private static readonly JsonSerializerOptions Options = new()
    {
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
        PropertyNameCaseInsensitive = true
    };

    public AuthClient(string baseUrl)
    {
        _http = new HttpClient { BaseAddress = new Uri(baseUrl.TrimEnd('/') + "/") };
    }

    public (AuthResponse? Response, string? Error) Register(string login, string password)
        => Post("auth/register", new AuthRequest { Login = login, Password = password });

    public (AuthResponse? Response, string? Error) Login(string login, string password)
        => Post("auth/login", new AuthRequest { Login = login, Password = password });

    private (AuthResponse? Response, string? Error) Post(string path, AuthRequest body)
    {
        try
        {
            var r = _http.PostAsJsonAsync(path, body, Options).GetAwaiter().GetResult();
            var json = r.Content.ReadAsStringAsync().GetAwaiter().GetResult();
            if (r.IsSuccessStatusCode)
                return (JsonSerializer.Deserialize<AuthResponse>(json, Options), null);

            using var doc = JsonDocument.Parse(json);
            var error = doc.RootElement.TryGetProperty("error", out var e) ? e.GetString() : "Unknown error";
            return (null, error);
        }
        catch (Exception ex)
        {
            Debug.WriteLine($"[AUTH {path}] {ex.Message}");
            return (null, "Сервис авторизации недоступен");
        }
    }
}

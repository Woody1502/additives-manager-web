using Microsoft.Extensions.Configuration;
using RecipeApp.WinForms.Services;

namespace RecipeApp.WinForms;

static class Program
{
    [STAThread]
    static void Main()
    {
        ApplicationConfiguration.Initialize();

        var config = new ConfigurationBuilder()
            .SetBasePath(AppContext.BaseDirectory)
            .AddJsonFile("appsettings.json", optional: false, reloadOnChange: false)
            .Build();

        var apiBaseUrl  = config["Api:BaseUrl"]  ?? "http://localhost:8080";
        var authBaseUrl = config["Auth:BaseUrl"] ?? "http://localhost:8081";

        var api  = new ApiClient(apiBaseUrl);
        var auth = new AuthClient(authBaseUrl);

        Application.Run(new Forms.MainForm(api, auth));
    }
}

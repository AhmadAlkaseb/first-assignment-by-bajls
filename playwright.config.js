const { defineConfig, devices } = require("@playwright/test");

module.exports = defineConfig({
  testDir: "./src/test/e2e",
  fullyParallel: true,
  retries: process.env.CI ? 2 : 0,
  use: {
    baseURL: "http://127.0.0.1:4173",
    trace: "on-first-retry"
  },
  projects: [
    {
      name: "chromium",
      use: { ...devices["Desktop Chrome"] }
    }
  ],
  webServer: {
    command: "npx http-server ./src/main/resources/static -p 4173 -c-1 --silent",
    url: "http://127.0.0.1:4173",
    reuseExistingServer: !process.env.CI
  }
});

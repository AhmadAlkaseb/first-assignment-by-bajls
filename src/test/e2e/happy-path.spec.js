import { test } from '@playwright/test';

test('test', async ({ page }) => {
  await page.goto('http://localhost:8080/');

  await page.getByRole('button', { name: 'Generate' }).click();

  await page.getByLabel('Choose a generator').selectOption('name-gender');
  await page.getByRole('button', { name: 'Generate' }).click();

  await page.getByLabel('Choose a generator').selectOption('name-gender-dob');
  await page.getByRole('button', { name: 'Generate' }).click();

  await page.getByLabel('Choose a generator').selectOption('cpr-name-gender');
  await page.getByRole('button', { name: 'Generate' }).click();

  await page.getByLabel('Choose a generator').selectOption('cpr-name-gender-dob');
  await page.getByRole('button', { name: 'Generate' }).click();

  await page.getByLabel('Choose a generator').selectOption('address');
  await page.getByRole('button', { name: 'Generate' }).click();

  await page.getByLabel('Choose a generator').selectOption('phone');
  await page.getByRole('button', { name: 'Generate' }).click();

  await page.getByLabel('Choose a generator').selectOption('person');
  await page.getByRole('button', { name: 'Generate' }).click();
  
  await page.getByLabel('Choose a generator').selectOption('person-bulk');
  await page.getByRole('spinbutton', { name: 'How many persons' }).click();
  await page.getByRole('spinbutton', { name: 'How many persons' }).fill('67');
  await page.getByRole('button', { name: 'Generate' }).click();
});
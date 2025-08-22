# Photo Backup (Android + PHP)

Token is already set to: `7fa8b92c63a42ff5e0d37e92d65a10cbe56dc9d2384f8b1b8f9a8d1f92c7f002`

## Configure
- Edit `android/app/src/main/java/com/example/photobackup/Uploader.kt` and set `SERVER_URL` to your HTTPS endpoint (currently `https://yourdomain.com/upload.php`).

## Server
- Place `server/html/upload.php` and `server/html/gallery.php` under your web root (e.g., `/var/www/html`).
- Ensure `/var/www/uploads` exists and is writable by the web server user.
- Use bearer token in requests (same token as above).

## Build Android
- Open `/android` in Android Studio.
- Generate a signed APK, install, run once, grant Photos permission.

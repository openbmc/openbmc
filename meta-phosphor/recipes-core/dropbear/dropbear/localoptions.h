// Disable CBC ciphers for modern security.
#define DROPBEAR_ENABLE_CBC_MODE 0

// Disable Chacha20-Poly1305 cipher.
#define DROPBEAR_CHACHA20POLY1305 0

#define DROPBEAR_SHA1_96_HMAC 0
#define DROPBEAR_SHA2_256_HMAC 1
#define DROPBEAR_SHA2_512_HMAC 1

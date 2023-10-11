// Customizations.  See dropbear project default_options.h

// Disable Chacha20-Poly1305 cipher.
#define DROPBEAR_CHACHA20POLY1305 0

#define DROPBEAR_SHA1_HMAC 0
#define DROPBEAR_SHA2_512_HMAC 1

#define DROPBEAR_RSA_SHA1 0

#define DROPBEAR_DH_GROUP14_SHA1 0

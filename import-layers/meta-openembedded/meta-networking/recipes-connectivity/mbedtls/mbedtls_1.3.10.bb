SUMMARY = "Lightweight crypto and SSL/TLS library"
DESCRIPTION = "mbedtls is a lean open source crypto library          \
for providing SSL and TLS support in your programs. It offers        \
an intuitive API and documented header files, so you can actually    \
understand what the code does. It features:                          \
                                                                     \
 - Symmetric algorithms, like AES, Blowfish, Triple-DES, DES, ARC4,  \
   Camellia and XTEA                                                 \
 - Hash algorithms, like SHA-1, SHA-2, RIPEMD-160 and MD5            \
 - Entropy pool and random generators, like CTR-DRBG and HMAC-DRBG   \
 - Public key algorithms, like RSA, Elliptic Curves, Diffie-Hellman, \
   ECDSA and ECDH                                                    \
 - SSL v3 and TLS 1.0, 1.1 and 1.2                                   \
 - Abstraction layers for ciphers, hashes, public key operations,    \
   platform abstraction and threading                                \
"

HOMEPAGE = "https://polarssl.org"
BUGTRACKER = "https://github.com/polarssl/polarssl/issues"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"

SECTION = "libdevel"

SRC_URI = "https://tls.mbed.org/download/mbedtls-${PV}-gpl.tgz"

SRC_URI[md5sum] = "19ebbc96feceb430ad958dfe89cb633f"
SRC_URI[sha256sum] = "746fd88e0c6623691fc56c4eed52e40a57b2da0ac80f6dd8995094aa6adb407e"

DEPENDS = "openssl"
RDEPENDS_${PN} += "libcrypto"
PROVIDES += "polarssl"
RPROVIDES_${PN} = "polarssl"
EXTRA_OECMAKE = "-DUSE_SHARED_POLARSSL_LIBRARY=on -DLIB_INSTALL_DIR=${baselib}"

inherit cmake

export OPENSSL_CONF="$OECORE_NATIVE_SYSROOT/usr/lib/ssl/openssl.cnf"
export OPENSSL_MODULES="$OECORE_NATIVE_SYSROOT/usr/lib/ossl-modules/"
export OPENSSL_ENGINES="$OECORE_NATIVE_SYSROOT/usr/lib/engines-3"

# Respect host env SSL_CERT_FILE/SSL_CERT_DIR first, then auto-detected host cert, then cert in buildtools
# CAFILE/CAPATH is auto-deteced when source buildtools
if [ -z "${SSL_CERT_FILE:-}" ]; then
	if [ -n "${CAFILE:-}" ];then
		export SSL_CERT_FILE="$CAFILE"
	elif [ -e "${OECORE_NATIVE_SYSROOT}/etc/ssl/certs/ca-certificates.crt" ];then
		export SSL_CERT_FILE="$OECORE_NATIVE_SYSROOT/usr/lib/ssl/certs/ca-certificates.crt"
	fi
fi

if [ -z "${SSL_CERT_DIR:-}" ]; then
	if [ -n "${CAPATH:-}" ];then
		export SSL_CERT_DIR="$CAPATH"
	elif [ -e "${OECORE_NATIVE_SYSROOT}/etc/ssl/certs/ca-certificates.crt" ];then
		export SSL_CERT_DIR="$OECORE_NATIVE_SYSROOT/usr/lib/ssl/certs"
	fi
fi

export BB_ENV_PASSTHROUGH_ADDITIONS="${BB_ENV_PASSTHROUGH_ADDITIONS:-} SSL_CERT_DIR SSL_CERT_FILE"

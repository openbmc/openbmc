# Respect host env GIT_SSL_CAINFO/GIT_SSL_CAPATH first, then auto-detected host cert, then cert in buildtools
# CAFILE/CAPATH is auto-deteced when source buildtools
if [ -z "${GIT_SSL_CAINFO:-}" ]; then
	if [ -n "${CAFILE:-}" ];then
		export GIT_SSL_CAINFO="$CAFILE"
	elif [ -e "${OECORE_NATIVE_SYSROOT}/etc/ssl/certs/ca-certificates.crt" ];then
		export GIT_SSL_CAINFO="${OECORE_NATIVE_SYSROOT}/etc/ssl/certs/ca-certificates.crt"
	fi
fi

if [ -z "${GIT_SSL_CAPATH:-}" ]; then
	if [ -n "${CAPATH:-}" ];then
		export GIT_SSL_CAPATH="$CAPATH"
	elif [ -e "${OECORE_NATIVE_SYSROOT}/etc/ssl/certs/ca-certificates.crt" ];then
		export GIT_SSL_CAPATH="${OECORE_NATIVE_SYSROOT}/etc/ssl/certs"
	fi
fi

export BB_ENV_PASSTHROUGH_ADDITIONS="${BB_ENV_PASSTHROUGH_ADDITIONS:-} GIT_SSL_CAINFO GIT_SSL_CAPATH"

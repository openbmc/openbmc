# Respect host env CURL_CA_BUNDLE/CURL_CA_PATH first, then auto-detected host cert, then cert in buildtools
# CAFILE/CAPATH is auto-deteced when source buildtools
if [ -z "${CURL_CA_PATH:-}" ]; then
	if [ -n "${CAFILE:-}" ];then
		export CURL_CA_BUNDLE="$CAFILE"
	elif [ -e "${OECORE_NATIVE_SYSROOT}/etc/ssl/certs/ca-certificates.crt" ];then
		export CURL_CA_BUNDLE="${OECORE_NATIVE_SYSROOT}/etc/ssl/certs/ca-certificates.crt"
	fi
fi

if [ -z "${CURL_CA_PATH:-}" ]; then
	if [ -n "${CAPATH:-}" ];then
		export CURL_CA_PATH="$CAPATH"
	elif [ -e "${OECORE_NATIVE_SYSROOT}/etc/ssl/certs/ca-certificates.crt" ];then
		export CURL_CA_PATH="${OECORE_NATIVE_SYSROOT}/etc/ssl/certs"
	fi
fi

export BB_ENV_PASSTHROUGH_ADDITIONS="${BB_ENV_PASSTHROUGH_ADDITIONS:-} CURL_CA_BUNDLE CURL_CA_PATH"

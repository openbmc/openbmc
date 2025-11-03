# Respect host env REQUESTS_CA_BUNDLE first, then auto-detected host cert, then cert in buildtools
# CAFILE/CAPATH is auto-deteced when source buildtools
if [ -z "${REQUESTS_CA_BUNDLE:-}" ]; then
	if [ -n "${CAFILE:-}" ];then
		export REQUESTS_CA_BUNDLE="$CAFILE"
	elif [ -e "${OECORE_NATIVE_SYSROOT}/etc/ssl/certs/ca-certificates.crt" ];then
		export REQUESTS_CA_BUNDLE="${OECORE_NATIVE_SYSROOT}/etc/ssl/certs/ca-certificates.crt"
	fi
fi

export BB_ENV_PASSTHROUGH_ADDITIONS="${BB_ENV_PASSTHROUGH_ADDITIONS:-} REQUESTS_CA_BUNDLE"

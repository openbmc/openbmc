# e3c246d4i has a 32MB flash and is space-constrained.  Drop features that
# are not essential to keep the image within the flash size budget.

# webui-vue compiles to a large JS/HTML bundle; use the Redfish/bmcweb API
# directly or a standalone web client instead.
IMAGE_FEATURES:remove:e3c246d4i = "obmc-webui"

# obmc-ikvm is not wired up on this board.
IMAGE_FEATURES:remove:e3c246d4i += "obmc-ikvm"

# telemetry daemon is not required for basic BMC operation.
IMAGE_FEATURES:remove:e3c246d4i += "obmc-telemetry"

# We don't need pkcs11 support on the BMC by default and it ends up causing a
# dependency chain that brings in Rust.
RDEPENDS:packagegroup-security-tpm2:remove = "tpm2-pkcs11"

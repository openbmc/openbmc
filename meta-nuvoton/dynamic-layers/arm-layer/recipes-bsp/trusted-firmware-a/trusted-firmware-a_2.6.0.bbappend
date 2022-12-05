SRC_URI:remove = "git://git.trustedfirmware.org/TF-A/trusted-firmware-a.git;protocol=https;name=tfa;branch=master"
SRC_URI:append = "git://github.com/Nuvoton-Israel/arm-trusted-firmware.git;protocol=https;name=tfa;branch=nuvoton"
SRCREV_tfa = "69e6d531f055bd6328d529b6f797b548c2c00aa1"

# Enable no warning for loading segment with RWX permissions
EXTRA_OEMAKE += "LDFLAGS='--no-warn-rwx-segments'"

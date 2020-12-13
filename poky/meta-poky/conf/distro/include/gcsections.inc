CFLAGS_SECTION_REMOVAL = "-ffunction-sections -fdata-sections"
LDFLAGS_SECTION_REMOVAL = "-Wl,--gc-sections"

# packages with build problems using sections
CFLAGS_SECTION_REMOVAL_pn-glibc = ""
LDFLAGS_SECTION_REMOVAL_pn-glibc = ""
CFLAGS_SECTION_REMOVAL_pn-cairo = ""
LDFLAGS_SECTION_REMOVAL_pn-cairo = ""
CFLAGS_SECTION_REMOVAL_pn-perl = ""
LDFLAGS_SECTION_REMOVAL_pn-perl = ""
CFLAGS_SECTION_REMOVAL_pn-grub-efi = ""
LDFLAGS_SECTION_REMOVAL_pn-grub-efi = ""
CFLAGS_SECTION_REMOVAL_pn-grub = ""
LDFLAGS_SECTION_REMOVAL_pn-grub = ""

# set default for target
CFLAGS_append_class-target = " ${CFLAGS_SECTION_REMOVAL}"
LDFLAGS_append_class-target = " ${LDFLAGS_SECTION_REMOVAL}"

# set default for nativesdk
CFLAGS_append_class-nativesdk = " ${CFLAGS_SECTION_REMOVAL}"
LDFLAGS_append_class-nativesdk = " ${LDFLAGS_SECTION_REMOVAL}"

# Pin to v249.5 to fix systemd-networkd issues.
SRCREV = "00b0393e65252bf631670604f58b844780b08c50"

# Fix https://github.com/systemd/systemd/issues/21113
SRC_URI += "file://0001-conf-parse-make-config_parse_many-optionally-save-st.patch"

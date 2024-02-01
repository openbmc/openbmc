FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:asrock-mmc-sdhci-reset-quirk = " file://disable-mmc-sdhci.cfg"

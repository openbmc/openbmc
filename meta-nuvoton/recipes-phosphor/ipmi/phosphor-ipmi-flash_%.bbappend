# NPCM8xx is using 0x0850 as PCI device-id.
NUVOTON_PCI_DID = "2128"
EXTRA_OEMESON:append:npcm8xx = " -Dnuvoton-pci-did=${NUVOTON_PCI_DID}"

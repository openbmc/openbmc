require ${@oe.utils.conditional('TCMODE', 'external-arm', 'grub-external-arm.inc', '', d)}

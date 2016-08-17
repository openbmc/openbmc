
Installing VirtualBox Guest Additions
=====================================

In order to use VirtualBox guest additions, they have to be build
first. They may have to be rebuilt each time the time you upgrade to
a new version of VirtualBox.

Make sure VM is configured with an Optical Drive. 

Please follow these steps to install the VirtualBox Guest Additions on the
Build Appliance VM:

1.  Boot VM, select root "Terminal" instead of the default "Terminal <2>"

2.  Insert Guest additions CD into VM optical drive:
    VM menu "Devices"->"Optical Drives"-> Select "VBoxGuestAdditions<version>.iso"

3.  Find your CDROM device. Typically it is /dev/hda for IDE. You can determine
    the actual name <cdromedev> by viewing the cdrom info:

        # cat /proc/sys/dev/cdrom/info

    Mount the cdrom drive:
        # mount -t iso9660 <cdromdev> /media/cdrom
    i.e.:
        # mount -t iso9660 /dev/hda /media/cdrom

4. Build the additions:

    First, we need to build of some prerequisite utilities.
    (This is only needed to be done once)

        # cd /lib/modules/<kernel-version>-yocto-standard/build
        # make scripts

    Now build the guest additions:

        # /media/cdrom/VBoxLinuxAdditions.run --nox11

    At this point, providing there were no build errors, the guest additions are 
    built and installed.

5. Check if vbox additions running:

        # /etc/init.d/vboxadd status

    If not running, try manually starting:

        # /etc/init.d/vboxadd start

6. Check if additons actually work, in particular folder sharing.

    Host: Devices->Shared Folders->Shared Folder Settings...
        Add any host folder and name it (i.e. "images")

    Guest VM: create mount point for the shared folder, i.e.:

        # mkdir ~/my-host

    Mount the shared folder: (Watch out for spelling: it's vboxsf NOT vboxfs)

        # mount -t vboxsf images ~/my-host

    Verify mount, should see the contents of the shared folder:

        # ls ~/my-host









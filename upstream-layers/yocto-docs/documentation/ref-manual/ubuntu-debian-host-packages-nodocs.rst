Here are the packages needed to build an image on a headless system
with a supported Ubuntu or Debian Linux distribution:

.. literalinclude:: ../tools/host_packages_scripts/ubuntu_essential.sh
   :language: shell

You also need to ensure you have the ``en_US.UTF-8`` locale enabled::

   $ locale --all-locales | grep en_US.utf8

If this is not the case, you can reconfigure the ``locales`` package to add it
(requires an interactive shell)::

   $ sudo dpkg-reconfigure locales

.. note::

   -  If you are not in an interactive shell, ``dpkg-reconfigure`` will
      not work as expected. To add the locale you will need to edit
      ``/etc/locale.gen`` file to add/uncomment the ``en_US.UTF-8`` locale.
      A naive way to do this as root is::

         $ echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen
         $ locale-gen

   -  If your build system has the ``oss4-dev`` package installed, you
      might experience QEMU build failures due to the package installing
      its own custom ``/usr/include/linux/soundcard.h`` on the Debian
      system. If you run into this situation, try either of these solutions::

         $ sudo apt build-dep qemu
         $ sudo apt remove oss4-dev


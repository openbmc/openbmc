Release 3.4 (honister)
======================

This section provides migration information for moving to the Yocto
Project 3.4 Release (codename "honister") from the prior release.

Override syntax changes
-----------------------

This release requires changes to the metadata to indicate where overrides are
being used in variable key names. This is done with the ``:`` character replacing
the use of ``_`` previously. This means that an entry like::

   SRC_URI_qemux86 = "file://somefile"

becomes::

   SRC_URI:qemux86 = "file://somefile"

since ``qemux86`` is an override. This applies to any use of override syntax so::

   SRC_URI_append = " file://somefile"
   SRC_URI_append_qemux86 = " file://somefile2"
   SRC_URI_remove_qemux86-64 = " file://somefile3"
   SRC_URI_prepend_qemuarm = "file://somefile4 "
   FILES_${PN}-ptest = "${bindir}/xyz"
   IMAGE_CMD_tar = "tar"
   BASE_LIB_tune-cortexa76 = "lib"
   SRCREV_pn-bash = "abc"
   BB_TASK_NICE_LEVEL_task-testimage = '0'

becomes::

   SRC_URI:append = " file://somefile"
   SRC_URI:append:qemux86 = " file://somefile2"
   SRC_URI:remove:qemux86-64 = " file://somefile3"
   SRC_URI:prepend:qemuarm = "file://somefile4 "
   FILES:${PN}-ptest = "${bindir}/xyz"
   IMAGE_CMD:tar = "tar"
   BASE_LIB:tune-cortexa76 = "lib"
   SRCREV:pn-bash = "abc"
   BB_TASK_NICE_LEVEL:task-testimage = '0'

This also applies to
:ref:`variable queries to the datastore <bitbake:bitbake-user-manual/bitbake-user-manual-metadata:functions for accessing datastore variables>`,
for example using ``getVar`` and similar so ``d.getVar("RDEPENDS_${PN}")``
becomes ``d.getVar("RDEPENDS:${PN}")``.

Whilst some of these are fairly obvious such as :term:`MACHINE` and :term:`DISTRO`
overrides, some are less obvious, for example the packaging variables such as
:term:`RDEPENDS`, :term:`FILES` and so on taking package names (e.g. ``${PN}``,
``${PN}-ptest``) as overrides. These overrides are not always in
:term:`OVERRIDES` but applied conditionally in specific contexts
such as packaging. ``task-<taskname>`` is another context specific override, the
context being specific tasks in that case. Tune overrides are another special
case where some code does use them as overrides but some does not. We plan to try
and make the tune code use overrides more consistently in the future.

To help with migration of layers there is a script in OE-Core. Once configured
with the overrides used by a layer, this can be run as::

   <oe-core>/scripts/contrib/convert-overrides.py <layerdir>

.. note::

   Please read the notes in the script as it isn't entirely automatic and it isn't
   expected to handle every case. In particular, it needs to be told which overrides
   the layer uses (usually machine and distro names/overrides) and the result should
   be carefully checked since it can be a little enthusiastic and will convert
   references to ``_append``, ``_remove`` and ``_prepend`` in function and variables names.

For reference, this conversion is important as it allows BitBake to know what is
an override and what is not. This should allow us to proceed with other syntax
improvements and simplifications for usability. It also means bitbake no longer
has to guess and maintain large lookup lists just in case ``functionname`` in
``my_functionname`` is an override and this should improve efficiency.

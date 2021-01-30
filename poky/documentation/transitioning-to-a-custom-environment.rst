.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

=============================================================
Transitioning to a custom environment for systems development
=============================================================

|

.. note::

   So you've finished the :doc:`brief-yoctoprojectqs/brief-yoctoprojectqs` and
   glanced over the document :doc:`what-i-wish-id-known`, the latter contains
   important information learned from other users. You're well prepared. But
   now, as you are starting your own project, it isn't exactly straightforward what
   to do. And, the documentation is daunting. We've put together a few hints to
   get you started.

#. **Make a list of the processor, target board, technologies, and capabilities
   that will be part of your project**.
   You will be finding layers with recipes and other metadata that support these
   things, and adding them to your configuration. (See #3)

#. **Set up your board support**.
   Even if you're using custom hardware, it might be easier to start with an
   existing target board that uses the same processor or at least the same
   architecture as your custom hardware. Knowing the board already has a
   functioning Board Support Package (BSP) within the project makes it easier
   for you to get comfortable with project concepts.

#. **Find and acquire the best BSP for your target**.
   Use the :yocto_home:`Yocto Project curated layer index
   </software-overview/layers/>` or even the `OpenEmbedded layer index
   <https://layers.openembedded.org>`_ to find and acquire the best BSP for your
   target board. The Yocto Project layer index BSPs are regularly validated. The
   best place to get your first BSP is from your silicon manufacturer or board
   vendor – they can point you to their most qualified efforts. In general, for
   Intel silicon use meta-intel, for Texas Instruments use meta-ti, and so
   forth. Choose a BSP that has been tested with the same Yocto Project release
   that you've downloaded. Be aware that some BSPs may not be immediately
   supported on the very latest release, but they will be eventually.

   You might want to start with the build specification that Poky provides
   (which is reference embedded distribution) and then add your newly chosen
   layers to that. Here is the information :ref:`about adding layers
   <dev-manual/dev-manual-common-tasks:Understanding and Creating Layers>`.

#. **Based on the layers you've chosen, make needed changes in your
   configuration**.
   For instance, you've chosen a machine type and added in the corresponding BSP
   layer. You'll then need to change the value of the ``MACHINE`` variable in your
   configuration file (build/local.conf) to point to that same machine
   type. There could be other layer-specific settings you need to change as
   well. Each layer has a ``README`` document that you can look at for this type of
   usage information.

#. **Add a new layer for any custom recipes and metadata you create**.
   Use the ``bitbake-layers create-layer`` tool for Yocto Project 2.4+
   releases. If you are using a Yocto Project release earlier than 2.4, use the
   ``yocto-layer create`` tool. The ``bitbake-layers`` tool also provides a number
   of other useful layer-related commands. See
   :ref:`dev-manual/dev-manual-common-tasks:creating a general layer using the
   \`\`bitbake-layers\`\` script` section.

#. **Create your own layer for the BSP you're going to use**.
   It is not common that you would need to create an entire BSP from scratch
   unless you have a *really* special device. Even if you are using an existing
   BSP, :ref:`create your own layer for the BSP <bsp-guide/bsp:creating a new
   bsp layer using the \`\`bitbake-layers\`\` script>`. For example, given a
   64-bit x86-based machine, copy the conf/intel-corei7-64 definition and give
   the machine a relevant name (think board name, not product name). Make sure
   the layer configuration is dependent on the meta-intel layer (or at least,
   meta-intel remains in your bblayers.conf). Now you can put your custom BSP
   settings into your layer and you can re-use it for different applications.

#. **Write your own recipe to build additional software support that isn't
   already available in the form of a recipe**.
   Creating your own recipe is especially important for custom application
   software that you want to run on your device. Writing new recipes is a
   process of refinement. Start by getting each step of the build process
   working beginning with fetching all the way through packaging. Next, run the
   software on your target and refine further as needed. See :ref:`Writing a New
   Recipe <dev-manual/dev-manual-common-tasks:writing a new recipe>` in the
   Yocto Project Development Tasks Manual for more information.

#. **Now you're ready to create an image recipe**.
   There are a number of ways to do this. However, it is strongly recommended
   that you have your own image recipe - don't try appending to existing image
   recipes. Recipes for images are trivial to create and you usually want to
   fully customize their contents.

#. **Build your image and refine it**.
   Add what's missing and fix anything that's broken using your knowledge of the
   :ref:`workflow <sdk-manual/sdk-extensible:using \`\`devtool\`\` in your sdk
   workflow>` to identify where issues might be occurring.

#. **Consider creating your own distribution**.
   When you get to a certain level of customization, consider creating your own
   distribution rather than using the default reference distribution.

   Distribution settings define the packaging back-end (e.g. rpm or other) as
   well as the package feed and possibly the update solution. You would create
   your own distribution in a new layer inheriting from Poky but overriding what
   needs to change for your distribution. If you find yourself adding a lot of
   configuration to your local.conf file aside from paths and other typical
   local settings, it's time to :ref:`consider creating your own distribution
   <dev-manual/dev-manual-common-tasks:creating your own distribution>`.

   You can add product specifications that can customize the distribution if
   needed in other layers. You can also add other functionality specific to the
   product. But to update the distribution, not individual products, you update
   the distribution feature through that layer.

#. **Congratulations! You're well on your way.**
   Welcome to the Yocto Project community.

.. include:: /boilerplate.rst

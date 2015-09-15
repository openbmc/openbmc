import gtk

class ProgressBar(gtk.Dialog):
    def __init__(self, parent):

        gtk.Dialog.__init__(self, flags=(gtk.DIALOG_MODAL | gtk.DIALOG_DESTROY_WITH_PARENT))
        self.set_title("Parsing metadata, please wait...")
        self.set_default_size(500, 0)
        self.set_transient_for(parent)
        self.progress = gtk.ProgressBar()
        self.vbox.pack_start(self.progress)
        self.show_all()

    def set_text(self, msg):
        self.progress.set_text(msg)

    def update(self, x, y):
        self.progress.set_fraction(float(x)/float(y))
        self.progress.set_text("%2d %%" % (x*100/y))

    def pulse(self):
        self.progress.set_text("Loading...")
        self.progress.pulse()

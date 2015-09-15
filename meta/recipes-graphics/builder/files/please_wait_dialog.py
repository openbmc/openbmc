#!/usr/bin/env python
import glib
import gtk
 
def destroy_window_cb(widget, event):
    gtk.main_quit()
 
def all_done_cb():
    gtk.main_quit()
 
def dialogue_ui():
    window = gtk.Window()
    window.set_title("Please wait...")
    window.connect("delete-event", destroy_window_cb)
    window.show()
    window.set_border_width(12)
 
    msg = "Please wait while BitBake initializes Hob"
    label = gtk.Label(msg)
    label.show()
    window.add(label)
 
    glib.timeout_add_seconds(10, all_done_cb)
 
if __name__ == "__main__":
    dialogue_ui()
    gtk.main()


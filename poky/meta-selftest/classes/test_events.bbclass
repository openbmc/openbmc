python test1_eventhandler() {
    bb.note("Test for bb.event.BuildStarted")
}
python test2_eventhandler() {
    bb.note("Test for bb.event.BuildCompleted")
}
python test3_eventhandler() {
    bb.note("Test for bb.event.InvalidEvent")
}

addhandler test1_eventhandler
test1_eventhandler[eventmask] = "bb.event.BuildStarted"
addhandler test2_eventhandler
test2_eventhandler[eventmask] = "bb.event.BuildCompleted"
addhandler test3_eventhandler
test3_eventhandler[eventmask] = "bb.event.InvalidEvent"

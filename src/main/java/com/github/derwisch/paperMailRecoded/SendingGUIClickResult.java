package com.github.derwisch.paperMailRecoded;

public enum SendingGUIClickResult {
    /**
     * Do nothing
     */
    NOTHING,
   
    /**
     * Send mail
     */
    SEND,
   
    /**
     * Cancel mail
     */
    CANCEL,
   
    /**
     * Open Enderchest, continue mail afterwards
     */
    OPEN_ENDERCHEST
}

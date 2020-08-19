package com.example.whatsappclone.listeners

interface ChatClickListener {
    fun onChatClicked(name: String?, otherUserId: String?, chatsImageUrl: String?,
                      chatsName: String?)
}

interface ContactsClickListener {
    fun onContactClicked(name: String?, phone: String?)
}

interface ProgressListener{
    fun onProgressUpdate(progress: Int)
}

interface FailureCallback {
    fun userError()
}
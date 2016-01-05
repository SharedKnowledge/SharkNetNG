package de.htw_berlin.sharkandroidstack.setup;

import android.content.Context;
import android.util.Log;

import net.sharkfw.knowledgeBase.sync.SyncKB;
import net.sharkfw.knowledgeBase.sync.SyncKP;

import de.htw_berlin.sharkandroidstack.android.KbTextViewWriter;
import de.htw_berlin.sharkandroidstack.sharkFW.peer.AndroidSharkEngine;

public class SharkStack {

    private AndroidSharkEngine _engine;
    private SyncKB _kb;
    private SyncKP _kp;
    private MySimpleKp _myKP;
    private KbTextViewWriter _kbTextViewWriter;

    public SharkStack(Context context, String name) {
        _engine = new AndroidSharkEngine(context);
        _engine.setConnectionTimeOut(20000); //TODO: needed?

        try {
            _kb = new KnowledgeBaseCreator().getKb(name);
            _kp = new SyncKP(_engine, _kb, 1000);
        } catch (net.sharkfw.system.SharkException e) {
            Log.d("Internal", "Setting up the SyncKB failed.");
        }

        _myKP = new MySimpleKp(_engine, _kb.getOwner(), _kp);
    }

    public SharkStack setTextViewWriter(KbTextViewWriter kbTextViewWriter) {
        _kbTextViewWriter = kbTextViewWriter;
        _kb.addListener(kbTextViewWriter);

        _myKP.setTextViewWriter(kbTextViewWriter);

        kbTextViewWriter.writeKbToTextView(_kb);
        return this;
    }

    public SharkStack start() {
        try {
            _engine.startWifiDirect(); //TODO: start your own stub here
            _kbTextViewWriter.appendToLogText("Engine started");
        } catch (Exception e) {
            _kbTextViewWriter.appendToLogText("Engine did not start: " + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }

    public void stop() {
        try {
            _engine.stopWifiDirect(); //TODO: stop your own stub here
            _kbTextViewWriter.appendToLogText("Engine stopped");
        } catch (Exception e) {
            _kbTextViewWriter.appendToLogText("Engine error on stop: " + e.getMessage());
            e.printStackTrace();
        }
        _engine.stop();
    }
}

package intrepid.io.rxjavademo.events;

import intrepid.io.rxjavademo.models.IpModel;

public class IpUpdatedEvent {
    private IpModel ipModel;

    public IpUpdatedEvent(IpModel ipModel) {
        this.ipModel = ipModel;
    }

    public IpModel getIpModel() {
        return ipModel;
    }
}

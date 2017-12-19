package cn.abcsys.devops.v2.deployer.cores.results;

import java.util.ArrayList;
import java.util.List;

public class ValidatorResultBean {
    private Boolean error;
    private List<String> messages;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<String> getMessage() {
        return messages;
    }

    public void setMessage(List<String> messages) {
        this.messages = messages;
    }

    public void putMessage(String message){
        if(this.messages == null)
            this.messages = new ArrayList<>();
        this.messages.add(message);
    }

}

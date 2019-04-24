package cn.cch.healthy.model;

public class Setmeal_Interest {
    private Integer id;

    private Integer smId;

    private Integer interestId;

    public Setmeal_Interest(Integer smId, Integer interestId) {
        this.smId = smId;
        this.interestId = interestId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSmId() {
        return smId;
    }

    public void setSmId(Integer smId) {
        this.smId = smId;
    }

    public Integer getInterestId() {
        return interestId;
    }

    public void setInterestId(Integer interestId) {
        this.interestId = interestId;
    }
}
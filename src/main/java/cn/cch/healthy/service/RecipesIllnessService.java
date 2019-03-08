package cn.cch.healthy.service;

import cn.cch.healthy.dao.RecipesIllnessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipesIllnessService {

    @Autowired
    RecipesIllnessMapper mapper;

    public List SelectByillId(int ill_id){ return mapper.selectByillId(ill_id); }
}

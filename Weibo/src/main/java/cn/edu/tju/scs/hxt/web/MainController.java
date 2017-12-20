package cn.edu.tju.scs.hxt.web;

import cn.edu.tju.scs.hxt.Config;
import cn.edu.tju.scs.hxt.WordsTable;
import cn.edu.tju.scs.hxt.enums.BizCode;
import cn.edu.tju.scs.hxt.pojo.dto.PositionInfo;
import cn.edu.tju.scs.hxt.pojo.vo.PageResultVO;
import cn.edu.tju.scs.hxt.pojo.vo.ResponseCodeVO;
import cn.edu.tju.scs.hxt.pojo.vo.WeiBo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by haoxiaotian on 2017/12/20 1:10.
 */

@Controller
public class MainController {

    @Autowired
    Config config;

    @Autowired
    WordsTable wordsTable;

    @RequestMapping(value = "/search", method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public @ResponseBody
    ResponseCodeVO<PageResultVO<WeiBo>> search( @RequestParam(value = "keyword", required = false) String keyWord, @RequestParam(value = "pageNum",required = false)Integer pageNum){
        pageNum = pageNum == null ? 0: pageNum;
        int pageSize = 10;
        int start,end;
        List<WeiBo> weiBos;
        if(!StringUtils.isEmpty(keyWord)) {
            String values = wordsTable.getValue(keyWord);
            String[] valuesArray = values.split(" ");
            PositionInfo info;
            String[] infoArray;
            String pos, startPos, stopPos;
            // 所有有当前词语的微博
            List<PositionInfo> results = new ArrayList<>();
            if (valuesArray.length > 0) {
                for (String item : valuesArray) {
                    infoArray = item.split("-->");
                    assert (infoArray.length == 2);
                    info = new PositionInfo();
                    info.setScore(Integer.valueOf(infoArray[1]));
                    pos = infoArray[0].substring(1, infoArray[0].length() - 1);
                    infoArray = pos.split(":");
                    info.setStartPos(Long.valueOf(infoArray[0]));
                    info.setLength(Integer.valueOf(infoArray[1]));
                    results.add(info);
                }
                Collections.sort(results);

                // 获取当前页起止位置
                start = pageSize * (pageNum - 1);
                end = pageSize * pageNum > results.size() ? results.size(): pageSize * pageNum;

                weiBos = wordsTable.randomRead(config.getInputFile(), results.subList(start,end));
            } else {
                weiBos = new ArrayList<>();
            }



            PageResultVO<WeiBo> result = new PageResultVO<WeiBo>(pageSize, results.size());
            result.setCurrentPage(pageNum);
            result.resetPageNo();
            result.setResults(weiBos);
            return new ResponseCodeVO<PageResultVO<WeiBo>>(BizCode.SUCCESS, result);
        }else{
            PageResultVO<WeiBo> result = new PageResultVO<WeiBo>(pageSize, 0);
            result.setResults(new ArrayList<WeiBo>());
            return new ResponseCodeVO<PageResultVO<WeiBo>>(BizCode.SUCCESS,result);
        }
    }
}

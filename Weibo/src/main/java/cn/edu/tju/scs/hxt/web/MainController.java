package cn.edu.tju.scs.hxt.web;

import cn.edu.tju.scs.hxt.enums.BizCode;
import cn.edu.tju.scs.hxt.pojo.PageResultVO;
import cn.edu.tju.scs.hxt.pojo.ResponseCodeVO;
import cn.edu.tju.scs.hxt.pojo.WeiBo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoxiaotian on 2017/12/20 1:10.
 */

@Controller
public class MainController {


    @RequestMapping(value = "/search", method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public @ResponseBody
    ResponseCodeVO<PageResultVO<WeiBo>> search( @RequestParam(value = "keyword", required = false) String keyWord, @RequestParam(value = "pageNum",required = false)Integer pageNum){
        WeiBo weiBo1 = new WeiBo("10020226680","1849442941","确实很恐怖~ //@斯伟江:奥巴马应当学习乐清钱云会案经验，埋葬后装摄像头，凡祭拜者一律送入关塔那摩基地。美帝out了。","Tue May 03 10:54:53 +0800 2011","0","0","2","10017042704","1225124135","【国际人士拉登“被海葬”】5月2日下午，拉登的海葬仪式在阿拉伯海北部美军航母“卡尔·文森”号上进行，整个过程大约五十分钟。美国之所以对拉登选择海葬，是因为目前没有国家愿意接收拉登尸体，同时也是为了防止他的坟墓成为恐怖主义者的圣地。一个死人还要被活人所提防，国际人士拉登确实很恐怖。","Tue May 03 09:44:22 +0800 2011","329","0","102");
        WeiBo weiBo2 = new WeiBo("10022198898","1371936187","没有接到报案就表示没有发生过，难怪药家鑫要灭口了","Tue May 03 11:36:44 +0800 2011","0","0","0","10021982028","1925878362","记者吴海平报道:经询问公安部门,目前已知,今早常熟路站未接到过持刀抢劫报案-----散了散了吧.","Tue May 03 11:32:02 +0800 2011","13","0","7");
        WeiBo weiBo3 = new WeiBo("1002471741","1687225704","对名人个人不良行为的关注更易促进包含着大多数人的大群体的行为纠正和正面倡导。我觉得真假是非还是很重要的，它至少影响了大多人对社会整体价值观的信心。从这个角度来说，唐骏事件，很多人内心其实都在等待一线光明。","Tue Jul 06 23:50:40 +0800 2010","0","0","0","1000678875","1056534590","学历真假不重要，重要的是这个人是唐骏，一个四处标榜自己是如何成功的年青学子的偶像。唐骏这次应该亲自说说清楚，这就是做偶像的代价。","Tue Jul 06 22:17:05 +0800 2010","0","0","13");
        WeiBo weiBo4 = new WeiBo("1002511538","1687225704","回复@tina叶婷:总算有机会和高层对个话哈。 //@tina叶婷:回复@amy果:你的思想在闪电了～～～ //@tina叶婷:回复@倪文弢:说的对！从另外一个层面讲，因为微软和盛大，造就了唐骏的成功，个人能力是一方面，平台也很重要！","Tue Jul 06 23:53:03 +0800 2010","0","0","0","1000678875","1056534590","学历真假不重要，重要的是这个人是唐骏，一个四处标榜自己是如何成功的年青学子的偶像。唐骏这次应该亲自说说清楚，这就是做偶像的代价。","Tue Jul 06 22:17:05 +0800 2010","0","0","13");
        WeiBo weiBo5 = new WeiBo("10029270334","1971239697","//@弹钢琴滴桃小蟹:罢工哈哈// @糗事微博 :心情不好时你想干啥？","Tue May 03 13:58:19 +0800 2011","0","0","0","9981466620","1775767857","【十二星座心情不好时想干啥】白羊座（寻短见）、金牛座（独自旅行）、双子座（玩消失）、巨蟹座（罢工）、狮子座（想骂人）、处女座（想找人抽）、天秤座（买醉）、天蝎座（乱花钱）、射手座（乱吼一气）、摩羯座（抓住某个人猛亲）、水瓶座（时空穿梭）、双鱼座（随便找个人私奔）","Mon May 02 15:40:53 +0800 2011","0","0","0");
        List<WeiBo> weiBos = new ArrayList<>();
        weiBos.add(weiBo1);
        weiBos.add(weiBo2);
        weiBos.add(weiBo3);
        weiBos.add(weiBo4);
        weiBos.add(weiBo5);
        PageResultVO<WeiBo> result = new PageResultVO<>(10, 1);
        result.setResults(weiBos);
        return new ResponseCodeVO<PageResultVO<WeiBo>>(BizCode.SUCCESS,result);
    }
}

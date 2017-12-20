package cn.edu.tju.scs.hxt.pojo.vo;

import java.util.List;

/**
 * Created by Takahashi on 2016/11/26.
 */
public class PageResultVO<T> {

    // 下一页
    private int pageNo;

    // 当前页
    private int currentPage;

    // 每页个个数
    private int pageSize;

    // 总条数
    private int totalCount;

    // 总页数
    private int pageCount;

    // 记录
    private List<T> results;

    public PageResultVO(int pageSize, int totalCount){
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.currentPage = 1;
        resetPageNo();
    }


    public int getPageCount() {
        return pageCount;
    }


    @Override
    public String toString() {
        return "PageResults{" +
                "pageNo=" + pageNo +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totalCount=" + totalCount +
                ", pageCount=" + pageCount +
                ", results=" + results +
                '}';
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;

    }

    public int getPageNo() {
        pageNo = currentPage+1;
        if (pageNo <= 0) {
            return 1;
        } else{
            return pageNo > pageCount ? pageCount : pageNo;
        }
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 0 ? 10 : pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void resetPageNo() {
        pageNo = currentPage + 1;
        pageCount = totalCount % pageSize == 0 ? totalCount / pageSize
                : totalCount / pageSize + 1;
    }
}

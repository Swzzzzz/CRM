package com.bjpowernode.crm.workbench.mapper;

import com.bjpowernode.crm.workbench.domain.Clue;

public interface ClueMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Wed May 11 21:00:55 CST 2022
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Wed May 11 21:00:55 CST 2022
     */
    int insertClue(Clue clue);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Wed May 11 21:00:55 CST 2022
     */
    int insertSelective(Clue record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Wed May 11 21:00:55 CST 2022
     */
    Clue selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Wed May 11 21:00:55 CST 2022
     */
    int updateByPrimaryKeySelective(Clue record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Wed May 11 21:00:55 CST 2022
     */
    int updateByPrimaryKey(Clue record);

    Clue selectClueDetailById(String id);

}
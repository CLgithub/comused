-- 创建分区表
create table T_CL_GZCDND
(
  host1  VARCHAR2(60),
  type1  VARCHAR2(50),
  device VARCHAR2(50),
  date1  DATE,
  value1 NUMBER(10,3)
)
partition by range(date1)
(
   partition p_01 values less than(to_date('2021-04-01','yyyy-mm-dd')) tablespace TBS_DATAHOUSE,
   partition p_02 values less than(to_date('2021-07-01','yyyy-mm-dd')) tablespace TBS_DATAHOUSE,
   partition p_03 values less than(to_date('2021-10-01','yyyy-mm-dd')) tablespace TBS_DATAHOUSE,
   partition p_04 values less than(to_date('2022-01-01','yyyy-mm-dd')) tablespace TBS_DATAHOUSE,
   partition p_05 values less than(to_date('2022-04-01','yyyy-mm-dd')) tablespace TBS_DATAHOUSE,
   partition p_06 values less than(to_date('2022-07-01','yyyy-mm-dd')) tablespace TBS_DATAHOUSE,
   partition p_07 values less than(to_date('2022-10-01','yyyy-mm-dd')) tablespace TBS_DATAHOUSE,
   partition p_08 values less than(to_date('2023-01-01','yyyy-mm-dd')) tablespace TBS_DATAHOUSE
)

-- 追加分区
alter table T_CL_GZCDND add PARTITION p_09 VALUES LESS THAN (TO_DATE('2023-04-01','YYYY-MM-DD')) tablespace TBS_DATAHOUSE;

-- 删除分区
alter table T_CL_GZCDND drop PARTITION p_09
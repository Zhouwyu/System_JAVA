package com.geekzhou.crm.mapper.mapstruct;

import com.geekzhou.crm.entity.Customer;
import com.geekzhou.crm.vo.CustomerInfoVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-27T20:30:49+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17 (Oracle Corporation)"
)
@Component
public class CustomerToVoMapperImpl implements CustomerToVoMapper {

    @Override
    public CustomerInfoVo toVo(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerInfoVo customerInfoVo = new CustomerInfoVo();

        customerInfoVo.setCustomerId( customer.getCustomerId() );
        customerInfoVo.setCustomerName( customer.getCustomerName() );
        customerInfoVo.setPhoneNum( customer.getPhoneNum() );
        customerInfoVo.setAddress( customer.getAddress() );
        customerInfoVo.setBusinessIndustry( customer.getBusinessIndustry() );

        return customerInfoVo;
    }

    @Override
    public List<CustomerInfoVo> toVoList(List<Customer> customers) {
        if ( customers == null ) {
            return null;
        }

        List<CustomerInfoVo> list = new ArrayList<CustomerInfoVo>( customers.size() );
        for ( Customer customer : customers ) {
            list.add( toVo( customer ) );
        }

        return list;
    }
}

package com.dine.DINERestaurant_Backend.user.service;

import com.dine.DINERestaurant_Backend.user.entity.UserAddress;
import com.dine.DINERestaurant_Backend.user.repository.UserAddressRepository;
import com.dine.DINERestaurant_Backend.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserAddressService {

    @Autowired
    private UserAddressRepository repo;

    public List<UserAddress> getAllByUser(User user) {
        return repo.findByUser(user);
    }

    public UserAddress getById(Integer id) {
        return repo.findById(id).orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Địa chỉ không tồn tại hoặc không thuộc sở hữu người dùng"));
    }

    public UserAddress getByDefaultAddress(User user) {
        List<UserAddress> addresses = repo.findByUser(user);
        for (UserAddress address : addresses) {
            if (address.getIsDefault()) {
                return address;
            }
        }
        return null;
    }

    public UserAddress save(UserAddress address) {
        return repo.save(address);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public UserAddress setDefaultAddress(User user, Integer id) {
        List<UserAddress> addresses = repo.findByUser(user);

        // Bỏ default của tất cả trước
        for (UserAddress a : addresses) {
            a.setIsDefault(false);
            repo.save(a);
        }

        UserAddress defaultAddress = repo.findById(id).orElseThrow();
        defaultAddress.setIsDefault(true);
        return repo.save(defaultAddress);
    }
}

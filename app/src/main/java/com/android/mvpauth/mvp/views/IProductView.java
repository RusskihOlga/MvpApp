package com.android.mvpauth.mvp.views;

import com.android.mvpauth.data.storage.dto.ProductDTO;

public interface IProductView extends IView{

    void showProductView(ProductDTO product);
    void updateProductCountView(ProductDTO productDTO);
}

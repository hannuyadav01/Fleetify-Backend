-- 1. companies
CREATE TABLE companies (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    gst_number VARCHAR(20),
    pan_number VARCHAR(15),
    address TEXT,
    phone VARCHAR(15),
    email VARCHAR(255),
    logo_url TEXT,
    status VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 2. users
CREATE TABLE users (
    id UUID PRIMARY KEY,
    company_id UUID REFERENCES companies(id),
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(15),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    fcm_token TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 3. vehicles
CREATE TABLE vehicles (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id),
    vehicle_number VARCHAR(20) NOT NULL UNIQUE,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INT,
    vehicle_type VARCHAR(50),
    fuel_type VARCHAR(50),
    load_capacity_tons DECIMAL(6,2),
    current_odometer_km INT,
    status VARCHAR(50) NOT NULL,
    purchase_date DATE,
    purchase_price DECIMAL(12,2),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 4. drivers
CREATE TABLE drivers (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    emergency_contact VARCHAR(15),
    address TEXT,
    date_of_birth DATE,
    joining_date DATE,
    monthly_salary DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    profile_photo_url TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 5. vehicle_documents
CREATE TABLE vehicle_documents (
    id UUID PRIMARY KEY,
    vehicle_id UUID NOT NULL REFERENCES vehicles(id),
    company_id UUID NOT NULL REFERENCES companies(id),
    doc_type VARCHAR(50) NOT NULL,
    document_number VARCHAR(100),
    issuing_authority VARCHAR(255),
    file_url TEXT NOT NULL,
    issue_date DATE,
    expiry_date DATE NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT TRUE,
    uploaded_by UUID REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 6. driver_documents
CREATE TABLE driver_documents (
    id UUID PRIMARY KEY,
    driver_id UUID NOT NULL REFERENCES drivers(id),
    company_id UUID NOT NULL REFERENCES companies(id),
    doc_type VARCHAR(50) NOT NULL,
    file_url TEXT NOT NULL,
    document_number VARCHAR(255),
    expiry_date DATE,
    is_current BOOLEAN NOT NULL DEFAULT TRUE,
    uploaded_by UUID REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 7. driver_vehicle_assignments
CREATE TABLE driver_vehicle_assignments (
    id UUID PRIMARY KEY,
    driver_id UUID NOT NULL REFERENCES drivers(id),
    vehicle_id UUID NOT NULL REFERENCES vehicles(id),
    company_id UUID NOT NULL REFERENCES companies(id),
    trip_id UUID, -- Foreign key constraint added later to resolve circular dependency
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    start_odometer INT,
    end_odometer INT,
    status VARCHAR(50) NOT NULL,
    assigned_by UUID REFERENCES users(id),
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 8. trips
CREATE TABLE trips (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id),
    vehicle_id UUID NOT NULL REFERENCES vehicles(id),
    driver_id UUID NOT NULL REFERENCES drivers(id),
    assignment_id UUID REFERENCES driver_vehicle_assignments(id),
    customer_id UUID REFERENCES users(id),
    source VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    route_details TEXT,
    distance_km DECIMAL(10,2),
    scheduled_start TIMESTAMP,
    actual_start TIMESTAMP,
    scheduled_end TIMESTAMP,
    actual_end TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    freight_amount DECIMAL(12,2),
    advance_paid DECIMAL(12,2),
    balance_due DECIMAL(12,2),
    payment_status VARCHAR(50),
    goods_type VARCHAR(255),
    weight_tons DECIMAL(10,2),
    tracking_code VARCHAR(20) UNIQUE,
    notes TEXT,
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 9. expenses
CREATE TABLE expenses (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id),
    trip_id UUID REFERENCES trips(id),
    vehicle_id UUID REFERENCES vehicles(id),
    expense_type VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    description TEXT,
    receipt_url TEXT,
    expense_date DATE NOT NULL,
    created_by_id UUID REFERENCES users(id),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verified_by_id UUID REFERENCES users(id),
    verified_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT chk_expense_target CHECK (trip_id IS NOT NULL OR vehicle_id IS NOT NULL)
);

-- 10. fuel_logs
CREATE TABLE fuel_logs (
    id UUID PRIMARY KEY,
    vehicle_id UUID NOT NULL REFERENCES vehicles(id),
    company_id UUID NOT NULL REFERENCES companies(id),
    trip_id UUID REFERENCES trips(id),
    quantity_litres DECIMAL(10,2) NOT NULL,
    cost_per_litre DECIMAL(10,2) NOT NULL,
    total_cost DECIMAL(10,2) NOT NULL,
    fuel_station VARCHAR(255),
    odometer_at_fill INT NOT NULL,
    filled_by_id UUID REFERENCES users(id),
    receipt_url TEXT,
    filled_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 11. maintenance_records
CREATE TABLE maintenance_records (
    id UUID PRIMARY KEY,
    vehicle_id UUID NOT NULL REFERENCES vehicles(id),
    company_id UUID NOT NULL REFERENCES companies(id),
    service_type VARCHAR(50) NOT NULL,
    description TEXT,
    cost DECIMAL(10,2),
    garage_name VARCHAR(255),
    odometer_at_service INT,
    service_date DATE NOT NULL,
    next_service_date DATE,
    next_service_km INT,
    bill_url TEXT,
    created_by_id UUID REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 12. alerts
CREATE TABLE alerts (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id),
    vehicle_id UUID REFERENCES vehicles(id),
    document_id UUID REFERENCES vehicle_documents(id),
    driver_id UUID REFERENCES drivers(id),
    alert_type VARCHAR(50) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    message TEXT,
    due_date DATE NOT NULL,
    is_resolved BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 13. invoices
CREATE TABLE invoices (
    id UUID PRIMARY KEY,
    trip_id UUID NOT NULL UNIQUE REFERENCES trips(id),
    company_id UUID NOT NULL REFERENCES companies(id),
    customer_id UUID REFERENCES users(id),
    invoice_number VARCHAR(50) UNIQUE,
    invoice_date DATE NOT NULL,
    subtotal DECIMAL(10,2),
    gst_rate DECIMAL(5,2),
    gst_amount DECIMAL(10,2),
    total_amount DECIMAL(10,2),
    due_date DATE,
    status VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 14. payments
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    invoice_id UUID NOT NULL REFERENCES invoices(id),
    company_id UUID NOT NULL REFERENCES companies(id),
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_mode VARCHAR(20) NOT NULL,
    reference_number VARCHAR(100),
    notes TEXT,
    received_by_id UUID REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 15. salary_records
CREATE TABLE salary_records (
    id UUID PRIMARY KEY,
    driver_id UUID NOT NULL REFERENCES drivers(id),
    company_id UUID NOT NULL REFERENCES companies(id),
    month INT NOT NULL,
    year INT NOT NULL,
    base_salary DECIMAL(10,2),
    allowances DECIMAL(10,2),
    deductions DECIMAL(10,2),
    net_salary DECIMAL(10,2),
    payment_status VARCHAR(50),
    payment_date DATE,
    paid_by_id UUID REFERENCES users(id),
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Add circular reference constraint
ALTER TABLE driver_vehicle_assignments
ADD CONSTRAINT fk_assignments_trip FOREIGN KEY (trip_id) REFERENCES trips(id);
